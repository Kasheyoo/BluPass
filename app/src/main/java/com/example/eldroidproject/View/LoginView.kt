package com.example.eldroidproject.View

interface LoginView {
    fun onLoginSuccess(role: String)
    fun onLoginPending(role: String)   // ✅ new
    fun showError(message: String)
}
