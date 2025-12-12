package com.example.eldroidproject.View
import com.example.eldroidproject.Model.Guest

interface GuestAccessView {
    interface View {
        fun onCodeGenerated(code: String)
        fun displayGuests(guests: List<Guest>)
        fun showError(message: String)
        // Navigation methods...
        fun navigateToHome()
        fun navigateToHistory()
        fun navigateToProfile()
        fun navigateToGuestAccess()
    }

    interface Presenter {
        fun generateCode(guestName: String, vehicle: String) // ✅ Update signature
        fun loadGuests()
        // Navigation methods...
        fun onHomeClicked()
        fun onHistoryClicked()
        fun onProfileClicked()
        fun onGuestAccessClicked()
    }
}