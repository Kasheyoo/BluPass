package com.example.eldroidproject.View

import com.example.eldroidproject.Model.Guest

interface GuestAccessView {

    interface View {
        // UI Updates
        fun displayGuests(guests: List<Guest>) // Updates the list
        fun onCodeGenerated(code: String)      // Updates the popup with the new code
        fun showError(message: String)         // Shows Toasts for errors

        // Navigation
        fun navigateToHome()
        fun navigateToHistory()
        fun navigateToProfile()
        fun navigateToGuestAccess()
    }

    interface Presenter {
        // Logic
        fun loadGuests()
        fun generateCode(guestName: String) // Logic to create a new invite

        // Navigation Logic
        fun onHomeClicked()
        fun onHistoryClicked()
        fun onProfileClicked()
        fun onGuestAccessClicked()
    }
}