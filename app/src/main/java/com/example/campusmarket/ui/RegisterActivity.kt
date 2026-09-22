package com.example.campusmarket.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.campusmarket.databinding.ActivityRegisterBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val googleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val token = runCatching { GoogleSignIn.getSignedInAccountFromIntent(result.data).result.idToken }.getOrNull()
        if (token == null) { message("Google Sign-In is not configured. See README.md."); return@registerForActivityResult }
        auth.signInWithCredential(GoogleAuthProvider.getCredential(token, null)).addOnCompleteListener { task ->
            if (task.isSuccessful) { saveUser(); openHome() } else message(task.exception?.localizedMessage ?: "Google registration failed.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater); setContentView(binding.root)
        binding.btnRegister.setOnClickListener { register() }
        binding.btnGoogleRegister.setOnClickListener { google() }
        binding.tvLogin.setOnClickListener { finish() }
    }

    private fun register() {
        val name = binding.etFullName.text?.toString()?.trim().orEmpty()
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString().orEmpty()
        val confirm = binding.etConfirmPassword.text?.toString().orEmpty()
        binding.fullNameInputLayout.error = null; binding.emailInputLayout.error = null; binding.passwordInputLayout.error = null; binding.confirmPasswordInputLayout.error = null
        if (name.length < 2) { binding.fullNameInputLayout.error = "Enter your full name"; return }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { binding.emailInputLayout.error = "Enter a valid email address"; return }
        if (password.length < 6) { binding.passwordInputLayout.error = "Password must contain at least 6 characters"; return }
        if (password != confirm) { binding.confirmPasswordInputLayout.error = "Passwords do not match"; return }
        setLoading(true)
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (!task.isSuccessful) { setLoading(false); message(task.exception?.localizedMessage ?: "Registration failed."); return@addOnCompleteListener }
            auth.currentUser?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())?.addOnCompleteListener {
                saveUser(name); setLoading(false); openHome()
            }
        }
    }

    private fun google() {
        val id = resources.getIdentifier("default_web_client_id", "string", packageName)
        if (id == 0) { message("Google Sign-In needs an OAuth client in Firebase. See README.md."); return }
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestIdToken(getString(id)).build()
        googleLauncher.launch(GoogleSignIn.getClient(this, options).signInIntent)
    }

    private fun saveUser(name: String = auth.currentUser?.displayName.orEmpty()) {
        val user = auth.currentUser ?: return
        FirebaseFirestore.getInstance().collection("users").document(user.uid).set(mapOf("displayName" to name, "email" to (user.email ?: ""), "createdAt" to System.currentTimeMillis()), SetOptions.merge())
    }
    private fun openHome() { startActivity(Intent(this, HomeActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }); finish() }
    private fun setLoading(value: Boolean) { binding.btnRegister.isEnabled = !value; binding.btnGoogleRegister.isEnabled = !value }
    private fun message(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}
