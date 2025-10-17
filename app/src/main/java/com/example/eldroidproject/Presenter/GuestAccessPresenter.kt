package com.example.eldroidproject.Presenter

import com.example.eldroidproject.Model.GuestRepository
import com.example.eldroidproject.View.GuestAccessView

class GuestAccessPresenter(
    private val view: GuestAccessView.View,
    private val repository: GuestRepository
) : GuestAccessView.Presenter {

    override fun loadGuests() {
        val guests = repository.getGuestList()
        view.displayGuests(guests)
    }

    override fun onHomeClicked() {
        view.navigateToHome()
    }

    override fun onHistoryClicked() {
        view.navigateToHistory()
    }

    override fun onProfileClicked() {
        view.navigateToProfile()
    }

    override fun onGuestAccessClicked() {
        view.navigateToGuestAccess()
    }
}
