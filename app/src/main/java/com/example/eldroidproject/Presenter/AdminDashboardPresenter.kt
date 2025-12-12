package com.example.eldroidproject.Presenter

import android.os.Handler
import android.os.Looper
import com.example.eldroidproject.Model.AdminRepository
import com.example.eldroidproject.View.AdminDashboardView

class AdminDashboardPresenter(
    private val view: AdminDashboardView,
    private val repository: AdminRepository
) {

    fun loadDashboard() {
        view.showLoading()

        // 1. Fetch Homeowners
        repository.fetchHomeowners(
            onSuccess = { users ->
                view.showHomeowners(users)
                view.hideLoading()
            },
            onFailure = { error ->
                view.showError(error)
                view.hideLoading()
            }
        )

        // 2. Fetch Guests
        repository.fetchAllGuests(
            onSuccess = { guests ->
                view.showGuests(guests)
            },
            onFailure = { error ->
                view.showError("Failed to load guests: $error")
            }
        )
    }

    // ✅ Gate Open Logic
    fun openGate() {
        // 1. Set status to OPEN
        repository.setGateOverride("open")
        view.showError("Command sent: Opening Gate...")

        // 2. Wait 5 seconds, then set back to IDLE
        Handler(Looper.getMainLooper()).postDelayed({
            repository.setGateOverride("idle")
        }, 5000) // 5000ms = 5 seconds
    }
}