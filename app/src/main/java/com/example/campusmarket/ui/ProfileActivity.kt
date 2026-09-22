package com.example.campusmarket.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.campusmarket.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.textfield.TextInputEditText
import timber.log.Timber

/**
 * Displays the profile information of the currently
 * authenticated CampusMarket user.
 *
 * The activity also provides navigation to My Listings,
 * Settings and Help & Support.
 */
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private val firebaseAuth =
        FirebaseAuth.getInstance()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityProfileBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        Timber.d("ProfileActivity started")

        setupToolbar()
        setupButtons()
        displayUserInformation()
    }

    override fun onResume() {
        super.onResume()

        if (::binding.isInitialized) {
            displayUserInformation()
        }
    }

    private fun setupToolbar() {

        binding.profileToolbar
            .setNavigationOnClickListener {
                finish()
            }
    }

    private fun setupButtons() {

        binding.btnMyListings.setOnClickListener {

            Timber.d(
                "My Listings selected"
            )

            val intent =
                Intent(
                    this,
                    MyListingsActivity::class.java
                )

            startActivity(intent)
        }

        binding.btnEditProfile.setOnClickListener {

            showEditProfileDialog()

            Timber.d(
                "Edit Profile selected"
            )
        }

        binding.btnSettings.setOnClickListener {

            Timber.d(
                "Settings selected"
            )

            val intent =
                Intent(
                    this,
                    SettingsActivity::class.java
                )

            startActivity(intent)
        }

        binding.btnHelpSupport.setOnClickListener {
            startActivity(Intent(this, HelpSupportActivity::class.java))

            Timber.d(
                "Help & Support selected"
            )
        }

        binding.btnLogout.setOnClickListener {

            logoutUser()
        }
    }

    private fun showEditProfileDialog() {
        val user = firebaseAuth.currentUser ?: return
        val input = TextInputEditText(this).apply {
            hint = "Full name"
            setText(user.displayName.orEmpty())
            setPadding(48, 24, 48, 8)
        }
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Edit profile")
            .setView(input)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save") { _, _ ->
                val name = input.text?.toString()?.trim().orEmpty()
                if (name.length < 2) {
                    Toast.makeText(this, "Enter your full name.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())
                    .addOnSuccessListener {
                        FirebaseFirestore.getInstance().collection("users").document(user.uid)
                            .set(mapOf("displayName" to name, "email" to (user.email ?: "")), com.google.firebase.firestore.SetOptions.merge())
                        displayUserInformation()
                        Toast.makeText(this, "Profile updated.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { Toast.makeText(this, it.localizedMessage ?: "Unable to update profile.", Toast.LENGTH_LONG).show() }
            }.show()
    }

    private fun displayUserInformation() {

        val currentUser =
            firebaseAuth.currentUser

        if (currentUser == null) {

            redirectToLogin()

            return
        }

        val email =
            currentUser.email
                ?: "user@example.com"

        val displayName =
            currentUser.displayName
                ?: email.substringBefore("@")
                    .ifBlank {
                        "CampusMarket User"
                    }

        binding.tvProfileName.text =
            displayName

        binding.tvProfileEmail.text =
            email

        binding.tvProfileRating.text =
            "4.8 ★"

        Timber.d(
            "Profile displayed for: $email"
        )
    }

    private fun logoutUser() {

        firebaseAuth.signOut()

        Timber.i(
            "User logged out"
        )

        Toast.makeText(
            this,
            "You have been logged out.",
            Toast.LENGTH_SHORT
        ).show()

        redirectToLogin()
    }

    private fun redirectToLogin() {

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
