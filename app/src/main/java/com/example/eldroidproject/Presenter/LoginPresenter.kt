package com.example.eldroidproject.Presenter

import android.util.Patterns
import com.example.eldroidproject.Model.AuthRepository
import com.example.eldroidproject.View.LoginView

class LoginPresenter(
    private val view: LoginView,
    private val repository: AuthRepository
) {

    fun loginWithEmail(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            view.showError("Please enter email and password")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.showError("Please enter a valid email address")
            return
        }

        repository.loginUserByEmail(
            email,
            password,
            onSuccess = { role -> view.onLoginSuccess(role) },
            onPending = { role -> view.onLoginPending(role) },
            onFailure = { error -> view.showError(error) }
        )
    }
}