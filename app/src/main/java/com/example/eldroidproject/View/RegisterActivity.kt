package com.example.eldroidproject

import android.app.Activity
import android.app.DatePickerDialog
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
import java.util.Calendar
import kotlin.random.Random

class RegisterActivity : Activity(), RegisterView {

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
        etFullName = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etMobile = findViewById(R.id.etMobile)
        etDOB = findViewById(R.id.etDOB)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvLoginLink = findViewById(R.id.tvLoginLink)

        presenter = RegisterPresenter(this, AuthRepository())

        etDOB.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                etDOB.setText(String.format("%02d / %02d / %04d", d, m + 1, y))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        btnSignUp.setOnClickListener {
            // 🔹 Generate a random UUID with 6 letters + 6 numbers
            val letters = (1..6).map { ('A'..'Z').random() }.joinToString("")
            val numbers = (1..6).map { Random.nextInt(0, 10) }.joinToString("")
            val customUUID = "$letters$numbers"

            val user = User(
                username = etFullName.text.toString(),
                email = etEmail.text.toString(),
                mobile = etMobile.text.toString(),
                dob = etDOB.text.toString(),
                uuid = customUUID
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
