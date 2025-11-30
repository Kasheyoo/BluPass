package com.example.eldroidproject

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.eldroidproject.Model.AuthRepository
import com.example.eldroidproject.Model.User
import com.example.eldroidproject.Presenter.RegisterPresenter
import com.example.eldroidproject.View.AdminDashboardActivity
import com.example.eldroidproject.View.RegisterView

class RegisterActivity : Activity(), RegisterView {

    private lateinit var presenter: RegisterPresenter
    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etMobile: EditText
    private lateinit var etRole: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvLoginLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        etFullName = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etMobile = findViewById(R.id.etMobile)
        etRole = findViewById(R.id.etRole)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvLoginLink = findViewById(R.id.tvLoginLink)

        presenter = RegisterPresenter(this, AuthRepository())

        // Role selection dialog (only Admin and Homeowner)
        etRole.setOnClickListener {
            val roles = arrayOf("Admin", "Homeowner")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select Role")
            builder.setItems(roles) { _, which ->
                etRole.setText(roles[which])
            }
            builder.show()
        }

        btnSignUp.setOnClickListener {
            val role = etRole.text.toString()

            val user = User(
                username = etFullName.text.toString(),
                email = etEmail.text.toString(),
                mobile = etMobile.text.toString(),
                role = role,
                status = if (role == "Admin") "approved" else "pending"
            )

            presenter.registerUser(
                etEmail.text.toString(),
                etPassword.text.toString(),
                etConfirmPassword.text.toString(),
                user
            )
        }

        // Login redirect
        tvLoginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onRegisterPending() {
        Toast.makeText(
            this,
            "Registration request submitted. Await admin approval.",
            Toast.LENGTH_LONG
        ).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onRegisterSuccess() {
        Toast.makeText(this, "Admin registration successful!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}