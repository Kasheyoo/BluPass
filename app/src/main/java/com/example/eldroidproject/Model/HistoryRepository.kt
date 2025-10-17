package com.example.eldroidproject.Model

class HistoryRepository {

    fun getHistoryRecords(): List<HistoryRecord> {
        // In the future, fetch this from Firebase Realtime DB or local Room DB
        return listOf(
            HistoryRecord("Entry", "ABC123", "26/09/2025 11:47:29"),
            HistoryRecord("Exit", "ABC123", "25/09/2025 19:20:48")
        )
    }
}
