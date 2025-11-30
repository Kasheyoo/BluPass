package com.example.eldroidproject.View

interface RegisterView {
    fun onRegisterPending()   // Homeowner registration request submitted
    fun onRegisterSuccess()   // Admin registration successful
    fun showError(message: String)
}