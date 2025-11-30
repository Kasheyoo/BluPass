package com.example.eldroidproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.eldroidproject.Model.AuthRepository
import com.example.eldroidproject.Presenter.LoginPresenter
import com.example.eldroidproject.View.LoginView
import com.example.eldroidproject.View.AdminDashboardActivity

class LoginActivity : Activity(), LoginView {

    private lateinit var presenter: LoginPresenter
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)

        presenter = LoginPresenter(this, AuthRepository())

        btnLogin.setOnClickListener {
            presenter.login(
                etEmail.text.toString().trim(),
                etPassword.text.toString().trim()
            )
        }

        btnSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onLoginSuccess(role: String) {
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

        if (role == "Admin") {
            // Admin goes directly to dashboard
            startActivity(Intent(this, AdminDashboardActivity::class.java))
        } else {
            // Homeowner goes to HomeActivity
            startActivity(Intent(this, HomeActivity::class.java))
        }
        finish()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}