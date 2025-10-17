package com.example.eldroidproject.Model

data class HistoryRecord(
    val type: String,         // "Entry" or "Exit"
    val vehicle: String,
    val date: String
)
