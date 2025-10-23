package com.example.eldroidproject.Model

import kotlin.uuid.Uuid

data class User(
    val username: String = "",
    val email: String = "",
    val mobile: String = "",
    val dob: String = "",
    val uuid: String = ""
)