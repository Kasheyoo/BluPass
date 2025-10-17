package com.example.eldroidproject.Presenter

import com.example.eldroidproject.Model.HistoryRepository
import com.example.eldroidproject.View.HistoryView

class HistoryPresenter(
    private val view: HistoryView.View,
    private val repository: HistoryRepository
) : HistoryView.Presenter {

    override fun loadHistory() {
        val records = repository.getHistoryRecords()
        view.displayHistory(records)
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
