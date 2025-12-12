package com.example.eldroidproject.View

import com.example.eldroidproject.Model.User

interface AdminManageView {
    fun showPendingList(users: List<User>)
    fun showMessage(message: String)
    fun refreshList() // Triggers a reload after approving/rejecting
}