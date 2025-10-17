package com.example.eldroidproject.View

import com.example.eldroidproject.Model.HistoryRecord

interface HistoryView {
    interface View {
        fun displayHistory(records: List<HistoryRecord>)
        fun navigateToHome()
        fun navigateToHistory()
        fun navigateToProfile()
        fun navigateToGuestAccess()
    }

    interface Presenter {
        fun loadHistory()
        fun onHomeClicked()
        fun onHistoryClicked()
        fun onProfileClicked()
        fun onGuestAccessClicked()
    }
}
