package com.example.eldroidproject.Presenter   // ✅ lowercase package

import com.example.eldroidproject.Model.AuthRepository
import com.example.eldroidproject.View.LoginView

class LoginPresenter(
    private val view: LoginView,
    private val repository: AuthRepository
) {

    fun loginWithUsername(username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            view.showError("Please enter username and password")
            return
        }

        repository.loginUserByUsername(
            username,
            password,
            onSuccess = { role -> view.onLoginSuccess(role) },
            onPending = { role -> view.onLoginPending(role) },
            onFailure = { error -> view.showError(error) }
        )
    }
}
