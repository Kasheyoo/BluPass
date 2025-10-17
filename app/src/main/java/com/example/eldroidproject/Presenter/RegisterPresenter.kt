package com.example.eldroidproject.Presenter

import com.example.eldroidproject.Model.AuthRepository
import com.example.eldroidproject.Model.User
import com.example.eldroidproject.View.RegisterView

class RegisterPresenter(
    private val view: RegisterView,
    private val repository: AuthRepository
) {

    fun register(fullName: String, email: String, mobile: String, dob: String, password: String, confirmPassword: String) {
        if (fullName.isEmpty() || email.isEmpty() || mobile.isEmpty() ||
            dob.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
        ) {
            view.showError("Please fill all fields")
            return
        }

        if (password.length < 6) {
            view.showError("Password must be at least 6 characters")
            return
        }

        if (password != confirmPassword) {
            view.showError("Passwords do not match")
            return
        }

        val user = User(fullName, email, mobile, dob)

        repository.registerUser(email, password, user,
            onSuccess = { view.onRegisterSuccess() },
            onFailure = { view.showError(it) }
        )
    }
}