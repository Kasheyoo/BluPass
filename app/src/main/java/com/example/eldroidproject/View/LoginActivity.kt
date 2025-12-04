package com.example.eldroidproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.eldroidproject.Model.AuthRepository   // ✅ lowercase package
import com.example.eldroidproject.Presenter.LoginPresenter
import com.example.eldroidproject.View.LoginView
import com.example.eldroidproject.View.AdminDashboardActivity

class LoginActivity : Activity(), LoginView {

    private lateinit var presenter: LoginPresenter
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)

        presenter = LoginPresenter(this, AuthRepository())

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            presenter.loginWithUsername(username, password)
        }

        btnSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    // ✅ Approved accounts
    override fun onLoginSuccess(role: String) {
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
        navigateToDashboard(role)
    }

    // ✅ Pending accounts
    override fun onLoginPending(role: String) {
        Toast.makeText(this, "Your account is still pending approval.", Toast.LENGTH_LONG).show()
        navigateToDashboard(role)
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToDashboard(role: String) {
        if (role == "Admin") {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
        } else {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        finish()
    }
}
