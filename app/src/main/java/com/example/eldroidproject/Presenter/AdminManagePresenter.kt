package com.example.eldroidproject.Presenter

import com.example.eldroidproject.Model.AdminRepository
import com.example.eldroidproject.View.AdminManageView

class AdminManagePresenter(
    private val view: AdminManageView,
    private val repository: AdminRepository
) {

    fun loadPendingUsers() {
        repository.fetchHomeowners(
            onSuccess = { allUsers ->
                // Filter specifically for "pending" status
                val pending = allUsers.filter { it.status == "pending" }
                view.showPendingList(pending)
            },
            onFailure = { view.showMessage("Error: $it") }
        )
    }

    fun approveUser(uid: String) {
        repository.updateHomeownerStatus(uid, "approved",
            onSuccess = {
                view.showMessage("User Approved")
                view.refreshList()
            },
            onFailure = { view.showMessage("Failed to approve") }
        )
    }

    fun rejectUser(uid: String) {
        repository.deleteHomeowner(uid,
            onSuccess = {
                view.showMessage("User Rejected")
                view.refreshList()
            },
            onFailure = { view.showMessage("Failed to reject") }
        )
    }
}