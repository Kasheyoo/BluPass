package com.example.eldroidproject.Presenter

import com.example.eldroidproject.View.ForgotPasswordContract
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordPresenter(
    private val auth: FirebaseAuth
) : ForgotPasswordContract.Presenter {

    private var view: ForgotPasswordContract.View? = null

    override fun attach(view: ForgotPasswordContract.View) {
        this.view = view
    }

    override fun detach() {
        this.view = null
    }

    override fun onSendCodeClicked(email: String) {
        val v = view ?: return

        // Basic validation
        if (email.isBlank()) {
            v.showEmailError("Email is required")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            v.showEmailError("Please enter a valid email")
            return
        }

        v.showEmailError(null)
        v.showLoading(true)

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                v.showLoading(false)

                if (task.isSuccessful) {
                    v.showSuccess("Password reset link sent to $email.\nPlease check your inbox or spam.")
                } else {
                    val errorMsg = task.exception?.localizedMessage
                        ?: "Failed to send reset email. Please try again."
                    v.showMessage(errorMsg)
                }
            }
    }
}