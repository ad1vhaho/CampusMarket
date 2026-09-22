package com.example.campusmarket.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import com.example.campusmarket.databinding.ActivitySettingsBinding
import com.example.campusmarket.utils.SettingsDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Displays the CampusMarket application settings.
 *
 * Settings include account options, notification preferences,
 * dark mode and support options.
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private lateinit var settingsDataStore: SettingsDataStore

    private val firebaseAuth =
        FirebaseAuth.getInstance()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        binding =
            ActivitySettingsBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        settingsDataStore =
            SettingsDataStore(applicationContext)

        Timber.d(
            "SettingsActivity started"
        )

        setupToolbar()
        setupButtons()
        loadSavedSettings()
    }

    private fun setupToolbar() {

        binding.settingsToolbar
            .setNavigationOnClickListener {
                finish()
            }
    }

    private fun setupButtons() {

        binding.rowEditProfile
            .setOnClickListener {

                startActivity(Intent(this, ProfileActivity::class.java))

                Timber.d(
                    "Edit Profile selected from Settings"
                )
            }

        binding.rowChangePassword
            .setOnClickListener {

                showChangePasswordDialog()

                Timber.d(
                    "Change Password selected"
                )
            }

        binding.rowLanguage
            .setOnClickListener {
                showLanguageDialog()

                Timber.d(
                    "Language selected"
                )
            }

        binding.switchNotifications
            .setOnCheckedChangeListener { _, enabled ->

                lifecycleScope.launch {

                    settingsDataStore
                        .setNotificationsEnabled(
                            enabled
                        )
                }

                Timber.d(
                    "Notifications enabled: $enabled"
                )
            }

        binding.switchDarkMode
            .setOnCheckedChangeListener { _, enabled ->

                lifecycleScope.launch {

                    settingsDataStore
                        .setDarkModeEnabled(
                            enabled
                        )
                }

                applyDarkMode(
                    enabled
                )

                Timber.d(
                    "Dark mode enabled: $enabled"
                )
            }

        binding.rowHelpCentre
            .setOnClickListener {
                startActivity(Intent(this, HelpSupportActivity::class.java))

                Timber.d(
                    "Help Centre selected"
                )
            }

        binding.rowAbout
            .setOnClickListener {

                showAboutDialog()
            }

        binding.btnSettingsLogout
            .setOnClickListener {

                logoutUser()
            }
    }

    private fun showLanguageDialog() {
        val names = arrayOf("English", "Afrikaans", "isiZulu", "Tshivenda")
        val tags = arrayOf("en", "af", "zu", "ve")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Choose language")
            .setItems(names) { _, position ->
                getSharedPreferences("campus_settings", MODE_PRIVATE).edit()
                    .putString("language_name", names[position]).apply()
                binding.tvLanguage.text = names[position]
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tags[position]))
            }.show()
    }

    private fun showChangePasswordDialog() {
        val user = firebaseAuth.currentUser ?: return
        val email = user.email
        if (email.isNullOrBlank() || user.providerData.none { it.providerId == "password" }) {
            Toast.makeText(this, "Password changes are only available for email/password accounts.", Toast.LENGTH_LONG).show()
            return
        }
        val container = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(48, 8, 48, 0) }
        val current = EditText(this).apply { hint = "Current password"; inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD }
        val next = EditText(this).apply { hint = "New password (6+ characters)"; inputType = current.inputType }
        container.addView(current); container.addView(next)
        androidx.appcompat.app.AlertDialog.Builder(this).setTitle("Change password").setView(container)
            .setNegativeButton("Cancel", null).setPositiveButton("Change") { _, _ ->
                val oldPassword = current.text.toString()
                val newPassword = next.text.toString()
                if (oldPassword.isBlank() || newPassword.length < 6) {
                    Toast.makeText(this, "Enter your current password and a new password of at least 6 characters.", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                user.reauthenticate(EmailAuthProvider.getCredential(email, oldPassword))
                    .continueWithTask { task -> if (!task.isSuccessful) throw task.exception ?: Exception("Authentication failed") else user.updatePassword(newPassword) }
                    .addOnSuccessListener { Toast.makeText(this, "Password changed.", Toast.LENGTH_SHORT).show() }
                    .addOnFailureListener { Toast.makeText(this, it.localizedMessage ?: "Unable to change password.", Toast.LENGTH_LONG).show() }
            }.show()
    }

    private fun loadSavedSettings() {

        lifecycleScope.launch {

            val notificationsEnabled =
                settingsDataStore
                    .notificationsEnabled
                    .first()

            val darkModeEnabled =
                settingsDataStore
                    .darkModeEnabled
                    .first()

            binding.switchNotifications
                .isChecked =
                notificationsEnabled

            binding.switchDarkMode
                .isChecked =
                darkModeEnabled

            binding.tvLanguage.text = getSharedPreferences("campus_settings", MODE_PRIVATE)
                .getString("language_name", "English")

            applyDarkMode(
                darkModeEnabled
            )

            Timber.d(
                "Saved settings loaded"
            )
        }
    }

    private fun applyDarkMode(
        enabled: Boolean
    ) {

        if (enabled) {

            AppCompatDelegate
                .setDefaultNightMode(
                    AppCompatDelegate
                        .MODE_NIGHT_YES
                )

        } else {

            AppCompatDelegate
                .setDefaultNightMode(
                    AppCompatDelegate
                        .MODE_NIGHT_NO
                )
        }
    }

    private fun showAboutDialog() {

        androidx.appcompat.app.AlertDialog
            .Builder(this)
            .setTitle("About CampusMarket")
            .setMessage(
                "CampusMarket is a student marketplace " +
                        "for buying and selling items within " +
                        "the student community."
            )
            .setPositiveButton(
                "OK",
                null
            )
            .show()
    }

    private fun logoutUser() {

        firebaseAuth.signOut()

        Timber.i(
            "User logged out from Settings"
        )

        Toast.makeText(
            this,
            "You have been logged out.",
            Toast.LENGTH_SHORT
        ).show()

        val intent =
            Intent(
                this,
                LoginActivity::class.java
            )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)

        finish()
    }
}
