package com.example.eldroidproject.Presenter

import com.example.eldroidproject.Model.AuthRepository // ✅ Import this
import com.example.eldroidproject.Model.GateRepository
import com.example.eldroidproject.View.HomeView

class HomePresenter(
    private val view: HomeView.View,
    private val gateRepository: GateRepository,
    private val authRepository: AuthRepository // ✅ Add this 3rd parameter
) : HomeView.Presenter {

    override fun loadGateStatus() {
        val gate = gateRepository.getGateStatus()
        view.displayGateStatus(gate)
    }

    // ✅ NOW USES AUTH REPOSITORY (The new solution)
    override fun loadUserInfo() {
        authRepository.fetchLotNumber { info ->
            view.setWelcomeMessage(info)
        }
    }

    // Navigation
    override fun onHomeClicked() = view.navigateToHome()
    override fun onHistoryClicked() = view.navigateToHistory()
    override fun onProfileClicked() = view.navigateToProfile()
    override fun onGuestAccessClicked() = view.navigateToGuestAccess()
}