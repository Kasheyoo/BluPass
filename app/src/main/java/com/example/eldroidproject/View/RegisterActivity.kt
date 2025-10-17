package com.example.eldroidproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eldroidproject.Model.AuthRepository
import com.example.eldroidproject.Model.User
import com.example.eldroidproject.Presenter.RegisterPresenter
import com.example.eldroidproject.View.RegisterView

class RegisterActivity : AppCompatActivity(), RegisterView {

    private lateinit var presenter: RegisterPresenter
    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etMobile: EditText
    private lateinit var etDOB: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvLoginLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etMobile = findViewById(R.id.etMobile)
        etDOB = findViewById(R.id.etDOB)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvLoginLink = findViewById(R.id.tvLoginLink)

        presenter = RegisterPresenter(this, AuthRepository())

        btnSignUp.setOnClickListener {
            val user = User(
                fullName = etFullName.text.toString(),
                email = etEmail.text.toString(),
                mobile = etMobile.text.toString(),
                dob = etDOB.text.toString()
            )
            presenter.registerUser(
                etEmail.text.toString(),
                etPassword.text.toString(),
                etConfirmPassword.text.toString(),
                user
            )
        }

        // ✅ This makes the "Login" text functional
        tvLoginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onRegisterSuccess() {
        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
