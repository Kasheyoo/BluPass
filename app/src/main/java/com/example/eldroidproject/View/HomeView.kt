package com.example.eldroidproject.View

import com.example.eldroidproject.Model.Gate

interface HomeView {
    interface View {
        fun displayGateStatus(gate: Gate)
        fun setWelcomeMessage(message: String) // ✅ Required

        fun navigateToHome()
        fun navigateToHistory()
        fun navigateToProfile()
        fun navigateToGuestAccess()
    }

    interface Presenter {
        fun loadGateStatus()
        fun loadUserInfo() // ✅ Required

        fun onHomeClicked()
        fun onHistoryClicked()
        fun onProfileClicked()
        fun onGuestAccessClicked()
    }
}