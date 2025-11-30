package com.example.eldroidproject.Presenter

import com.example.eldroidproject.Model.AuthRepository
import com.example.eldroidproject.View.LoginView

class LoginPresenter(
    private val view: LoginView,
    private val repository: AuthRepository
) {

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            view.showError("Please enter email and password")
            return
        }

        repository.loginUser(
            email,
            password,
            onSuccess = { role -> view.onLoginSuccess(role) },
            onFailure = { error -> view.showError(error) }
        )
    }
}