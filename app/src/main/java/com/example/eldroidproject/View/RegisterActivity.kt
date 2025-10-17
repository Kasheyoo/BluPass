package com.example.eldroidproject

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.eldroidproject.Model.AuthRepository
import com.example.eldroidproject.Presenter.RegisterPresenter
import com.example.eldroidproject.View.RegisterView
import java.util.Calendar

class RegisterActivity : Activity(), RegisterView {

    private lateinit var presenter: RegisterPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etMobile = findViewById<EditText>(R.id.etMobile)
        val etDOB = findViewById<EditText>(R.id.etDOB)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)

        presenter = RegisterPresenter(this, AuthRepository())

        // Date Picker
        etDOB.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                etDOB.setText(String.format("%02d / %02d / %04d", d, m + 1, y))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        btnSignUp.setOnClickListener {
            presenter.register(
                etFullName.text.toString(),
                etEmail.text.toString(),
                etMobile.text.toString(),
                etDOB.text.toString(),
                etPassword.text.toString(),
                etConfirmPassword.text.toString()
            )
        }
    }

    override fun onRegisterSuccess() {
        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
