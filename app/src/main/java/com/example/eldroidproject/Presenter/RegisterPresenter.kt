package com.example.eldroidproject.Presenter

import com.example.eldroidproject.Model.AuthRepository
import com.example.eldroidproject.Model.User
import com.example.eldroidproject.View.RegisterView

class RegisterPresenter(
    private val view: RegisterView,
    private val authRepository: AuthRepository
) {

    fun registerUser(
        email: String,
        mobile: String,
        role: String,
        lotNumber: String?,
        password: String,
        confirmPassword: String
    ) {
        // --- Validation Logic (Username removed) ---
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.showError("Invalid or empty email.")
            return
        }
        if (mobile.isBlank()) {
            view.showError("Mobile number is required.")
            return
        }
        if (role.isBlank()) {
            view.showError("Role is required.")
            return
        }
        if (role == "Homeowner" && lotNumber.isNullOrBlank()) {
            view.showError("Lot number is required for Homeowners.")
            return
        }
        if (password.isBlank() || password.length < 6) {
            view.showError("Password must be at least 6 characters.")
            return
        }
        if (password != confirmPassword) {
            view.showError("Passwords do not match.")
            return
        }

        // --- Create User Object ---
        val user = User(
            email = email,
            mobile = mobile,
            role = role,
            lotNumber = if (role == "Homeowner") lotNumber else null,
            password = password, // Still keeping this for testing as requested
            status = if (role == "Admin") "approved" else "pending"
        )

        // --- Send to Repository ---
        authRepository.registerUser(
            email = email,
            password = password,
            user = user,
            onSuccess = {
                if (role == "Admin") view.onRegisterSuccess() else view.onRegisterPending()
            },
            onFailure = { error -> view.showError(error) }
        )
    }
}