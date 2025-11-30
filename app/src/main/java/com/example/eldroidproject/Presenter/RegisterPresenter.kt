package com.example.eldroidproject.Presenter

import com.example.eldroidproject.Model.AuthRepository
import com.example.eldroidproject.Model.User
import com.example.eldroidproject.View.RegisterView

class RegisterPresenter(
    private val view: RegisterView,
    private val authRepository: AuthRepository
) {

    fun registerUser(email: String, password: String, confirmPassword: String, user: User) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            view.showError("Please fill in all fields.")
            return
        }

        if (password != confirmPassword) {
            view.showError("Passwords do not match.")
            return
        }

        authRepository.registerUser(
            email = email,
            password = password,
            user = user,
            onSuccess = {
                if (user.role == "Admin") {
                    view.onRegisterSuccess()   // Admin → dashboard
                } else {
                    view.onRegisterPending()   // Homeowner → pending message
                }
            },
            onFailure = { error -> view.showError(error) }
        )
    }
}