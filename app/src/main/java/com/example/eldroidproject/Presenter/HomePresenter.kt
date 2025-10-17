package com.example.eldroidproject.Presenter

import com.example.eldroidproject.Model.GateRepository
import com.example.eldroidproject.View.HomeView

class HomePresenter(
    private val view: HomeView.View,
    private val repository: GateRepository
) : HomeView.Presenter {

    override fun loadGateStatus() {
        val gate = repository.getGateStatus()
        view.displayGateStatus(gate)
    }

    override fun onOpenGateClicked() {
        repository.updateGateStatus("Open")
        val updatedGate = repository.getGateStatus()
        view.displayGateStatus(updatedGate)
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
