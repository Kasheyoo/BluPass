package com.example.eldroidproject.Model

data class Guest(
    val name: String = "",
    val vehicle: String = "",
    val lotNumber: String = "",
    val code: String = "",
    val status: String = "active",
    val timestamp: Long = 0
)