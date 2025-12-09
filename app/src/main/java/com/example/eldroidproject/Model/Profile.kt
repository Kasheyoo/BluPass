package com.example.eldroidproject.Model

data class Profile(
    val email: String = "",
    val phone: String = "",
    val plateNumber: String = "",
    val carModel: String = "",
    val uuid: String = "" // Added for BLE UUID
)