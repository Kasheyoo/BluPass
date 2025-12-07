package com.example.eldroidproject.View

import android.content.Intent
import android.os.Bundle
import android.widget.Button   // ✅ add this
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eldroidproject.LoginActivity
import com.example.eldroidproject.Presenter.ForgotPasswordPresenter
import com.example.eldroidproject.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : AppCompatActivity(), ForgotPasswordContract.View {

    private lateinit var presenter: ForgotPasswordContract.Presenter

    private lateinit var etEmail: EditText
    private lateinit var buttonCode: Button          // ✅ change type to Button
    private lateinit var forgotGoLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etEmail = findViewById(R.id.etEmail)
        buttonCode = findViewById(R.id.button_code)  // ✅ now matches <Button> in XML
        forgotGoLogin = findViewById(R.id.forgot_go_login)

        presenter = ForgotPasswordPresenter(FirebaseAuth.getInstance())
        presenter.attach(this)

        buttonCode.setOnClickListener {
            val email = etEmail.text.toString().trim()
            presenter.onSendCodeClicked(email)
        }

        forgotGoLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun showLoading(isLoading: Boolean) {
        buttonCode.isEnabled = !isLoading
        buttonCode.text = if (isLoading) "Sending..." else "Send Code"
    }

    override fun showEmailError(message: String?) {
        etEmail.error = message
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showSuccess(message: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_reset_link_sent, null)

        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<TextView>(R.id.messageText).text = message

        dialogView.findViewById<Button>(R.id.btnContinue).setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        dialog.show()
    }


    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
    }
}
