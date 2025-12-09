package com.example.eldroidproject

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.eldroidproject.Model.AuthRepository
import com.example.eldroidproject.Presenter.LoginPresenter
import com.example.eldroidproject.View.AdminDashboardActivity
import com.example.eldroidproject.View.ForgotPassword
import com.example.eldroidproject.View.LoginView

class LoginActivity : Activity(), LoginView {

    private lateinit var presenter: LoginPresenter
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Views
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)

        presenter = LoginPresenter(this, AuthRepository())

        forgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPassword::class.java))
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            presenter.loginWithEmail(email, password)
        }

        btnSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    // --- View Implementations ---

    override fun onLoginSuccess(role: String) {
        runOnUiThread {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
            navigateToDashboard(role)
        }
    }

    // SCENARIO: Account Pending -> Show Custom Dialog
    override fun onLoginPending(role: String) {
        runOnUiThread {
            showPendingDialog()
        }
    }

    // SCENARIO: Any Error (Wrong password, Not registered, etc.) -> Show Toast
    override fun showError(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    // --- Helper for Pending Dialog ---
    private fun showPendingDialog() {
        try {
            if (isFinishing) return

            // Inflate layout (Only need dialog_account_pending.xml now)
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_acount_pending, null)

            val builder = AlertDialog.Builder(this)
            builder.setView(dialogView)
            builder.setCancelable(false)

            val alert = builder.create()
            alert.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alert.show()

            val btnOk = dialogView.findViewById<Button>(R.id.btnDialogOk)
            btnOk.setOnClickListener {
                alert.dismiss()
            }
        } catch (e: Exception) {
            Log.e("LoginActivity", "Error showing dialog: ${e.message}")
        }
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