package com.example.eldroidproject

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.eldroidproject.Model.AuthRepository
import com.example.eldroidproject.Presenter.RegisterPresenter
import com.example.eldroidproject.View.RegisterView

class RegisterActivity : Activity(), RegisterView {

    private lateinit var presenter: RegisterPresenter

    // UI Components
    private lateinit var etEmail: EditText
    private lateinit var etMobile: EditText
    private lateinit var etRole: EditText
    private lateinit var etLotNumber: EditText
    private lateinit var tvLotNumberLabel: TextView
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvLoginLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 1. Initialize Views (Username removed)
        etEmail = findViewById(R.id.etEmail)
        etMobile = findViewById(R.id.etMobile)
        etRole = findViewById(R.id.etRole)
        etLotNumber = findViewById(R.id.etLotNumber)
        tvLotNumberLabel = findViewById(R.id.tvLotNumberLabel)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvLoginLink = findViewById(R.id.tvLoginLink)

        // 2. Initialize Presenter
        presenter = RegisterPresenter(this, AuthRepository())

        // 3. Role Selection Logic
        etRole.setOnClickListener {
            val roles = arrayOf("Admin", "Homeowner")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select Role")
            builder.setItems(roles) { _, which ->
                val selectedRole = roles[which]
                etRole.setText(selectedRole)

                if (selectedRole == "Homeowner") {
                    tvLotNumberLabel.visibility = View.VISIBLE
                    etLotNumber.visibility = View.VISIBLE
                } else {
                    tvLotNumberLabel.visibility = View.GONE
                    etLotNumber.visibility = View.GONE
                }
            }
            builder.show()
        }

        // 4. Sign Up Button Click
        btnSignUp.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val mobile = etMobile.text.toString().trim()
            val role = etRole.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            val lotNumber = if (role == "Homeowner") {
                etLotNumber.text.toString().trim()
            } else {
                null
            }

            presenter.registerUser(
                email = email,
                mobile = mobile,
                role = role,
                lotNumber = lotNumber,
                password = password,
                confirmPassword = confirmPassword
            )
        }

        // 5. Navigate to Login
        tvLoginLink.setOnClickListener {
            navigateLogin()
        }
    }

    override fun onRegisterPending() {
        Toast.makeText(this, "Registration submitted! Wait for approval.", Toast.LENGTH_LONG).show()
        navigateLogin()
    }

    override fun onRegisterSuccess() {
        Toast.makeText(this, "Admin registered successfully!", Toast.LENGTH_SHORT).show()
        navigateLogin()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}