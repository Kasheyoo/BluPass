package com.example.eldroidproject

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.eldroidproject.Model.AuthRepository
import com.example.eldroidproject.Model.User
import com.example.eldroidproject.Presenter.RegisterPresenter
import com.example.eldroidproject.View.RegisterView

class RegisterActivity : Activity(), RegisterView {

    private lateinit var presenter: RegisterPresenter
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etMobile: EditText
    private lateinit var etRole: EditText
    private lateinit var etLotNumber: EditText
    private lateinit var tvLotNumberLabel: TextView
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvLoginLink: TextView
    private lateinit var btnGenerateUsername: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etMobile = findViewById(R.id.etMobile)
        etRole = findViewById(R.id.etRole)
        etLotNumber = findViewById(R.id.etLotNumber)
        tvLotNumberLabel = findViewById(R.id.tvLotNumberLabel)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvLoginLink = findViewById(R.id.tvLoginLink)
        btnGenerateUsername = findViewById(R.id.btnGenerateUsername)

        presenter = RegisterPresenter(this, AuthRepository())

        // Role selection dialog
        etRole.setOnClickListener {
            val roles = arrayOf("Admin", "Homeowner")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select Role")
            builder.setItems(roles) { _, which ->
                etRole.setText(roles[which])

                if (roles[which] == "Homeowner") {
                    tvLotNumberLabel.visibility = View.VISIBLE
                    etLotNumber.visibility = View.VISIBLE
                } else {
                    tvLotNumberLabel.visibility = View.GONE
                    etLotNumber.visibility = View.GONE
                }
            }
            builder.show()
        }

        // Generate Username button
        btnGenerateUsername.setOnClickListener {
            val role = etRole.text.toString()
            val randomNum = (10..99).random()

            val generatedUsername = when (role) {
                "Admin" -> "admin_$randomNum"
                "Homeowner" -> {
                    val lot = etLotNumber.text.toString().ifEmpty { "lot" }
                    "${lot}_$randomNum"
                }
                else -> "user_$randomNum"
            }

            etUsername.setText(generatedUsername)
        }

        // Sign Up button
        btnSignUp.setOnClickListener {
            val role = etRole.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (password != confirmPassword) {
                showError("Passwords do not match")
                return@setOnClickListener
            }

            val user = User(
                email = etEmail.text.toString(),
                mobile = etMobile.text.toString(),
                role = role,
                lotNumber = if (role == "Homeowner") etLotNumber.text.toString() else null,
                username = etUsername.text.toString(),
                password = etPassword.text.toString(),
                status = if (role == "Admin") "approved" else "pending"
            )

            presenter.registerUser(
                etEmail.text.toString(),
                password,
                confirmPassword,
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
