package com.example.eldroidproject.Model

data class User(
    val uid: String = "",
    val email: String = "",
    val mobile: String = "",
    val role: String = "",
    val lotNumber: String? = null,
    val status: String = "pending"
)