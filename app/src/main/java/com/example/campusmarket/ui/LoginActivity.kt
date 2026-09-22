package com.example.campusmarket.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.campusmarket.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val googleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val token = runCatching { GoogleSignIn.getSignedInAccountFromIntent(result.data).result.idToken }.getOrNull()
        if (token == null) { message("Google Sign-In is not configured. Add the app SHA fingerprint in Firebase and download a new google-services.json."); return@registerForActivityResult }
        setLoading(true)
        auth.signInWithCredential(GoogleAuthProvider.getCredential(token, null)).addOnCompleteListener { task ->
            setLoading(false)
            if (task.isSuccessful) openHome() else message(task.exception?.localizedMessage ?: "Google Sign-In failed.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater); setContentView(binding.root)
        if (auth.currentUser != null) { openHome(); return }
        binding.btnLogin.setOnClickListener { loginWithEmail() }
        binding.btnGoogleLogin.setOnClickListener { startGoogleSignIn() }
        binding.tvForgotPassword.setOnClickListener { resetPassword() }
        binding.tvRegister.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
    }

    private fun loginWithEmail() {
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString().orEmpty()
        binding.emailInputLayout.error = null; binding.passwordInputLayout.error = null
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { binding.emailInputLayout.error = "Enter a valid email address"; return }
        if (password.length < 6) { binding.passwordInputLayout.error = "Password must contain at least 6 characters"; return }
        setLoading(true)
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            setLoading(false)
            if (task.isSuccessful) openHome() else message(task.exception?.localizedMessage ?: "Login failed.")
        }
    }

    private fun resetPassword() {
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { binding.emailInputLayout.error = "Enter your email first"; return }
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task -> message(if (task.isSuccessful) "Password reset email sent." else task.exception?.localizedMessage ?: "Unable to send reset email.") }
    }

    private fun startGoogleSignIn() {
        val id = resources.getIdentifier("default_web_client_id", "string", packageName)
        if (id == 0) { message("Google Sign-In needs an OAuth client in Firebase. See README.md."); return }
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestIdToken(getString(id)).build()
        googleLauncher.launch(GoogleSignIn.getClient(this, options).signInIntent)
    }

    private fun openHome() { startActivity(Intent(this, HomeActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }); finish() }
    private fun setLoading(value: Boolean) { binding.btnLogin.isEnabled = !value; binding.btnGoogleLogin.isEnabled = !value }
    private fun message(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}
