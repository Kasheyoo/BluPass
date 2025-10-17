package com.example.eldroidproject.View

import com.example.eldroidproject.Model.Guest

interface GuestAccessView {
    interface View {
        fun displayGuests(guests: List<Guest>)
        fun navigateToHome()
        fun navigateToHistory()
        fun navigateToProfile()
        fun navigateToGuestAccess()
    }

    interface Presenter {
        fun loadGuests()
        fun onHomeClicked()
        fun onHistoryClicked()
        fun onProfileClicked()
        fun onGuestAccessClicked()
    }
}
