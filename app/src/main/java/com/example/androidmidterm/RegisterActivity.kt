package com.example.androidmidterm

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val registerButton: Button = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (isValidEmail(email) && isValidPassword(password)) {
                authViewModel.register(email, password) { user ->
                    if (user != null) {
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                        // Navigate to login activity
                        finish()
                    } else {
                        Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                if (!isValidEmail(email)) {
                    Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
                }
                if (!isValidPassword(password)) {
                    Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
}