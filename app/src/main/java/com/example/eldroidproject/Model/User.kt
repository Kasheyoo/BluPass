package com.example.eldroidproject.Model

data class User(
    val email: String = "",
    val mobile: String = "",
    val role: String = "",
    val lotNumber: String? = null,
    val username: String = "",
    val password: String = "",
    val status: String = "pending"
)
