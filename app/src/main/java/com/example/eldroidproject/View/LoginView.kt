package com.example.eldroidproject.View

interface LoginView {
    fun onLoginSuccess(role: String)   // pass role back
    fun showError(message: String)
}