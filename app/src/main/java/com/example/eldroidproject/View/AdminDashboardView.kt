package com.example.eldroidproject.View

import com.example.eldroidproject.Model.Guest
import com.example.eldroidproject.Model.User

interface AdminDashboardView {
    fun showGuests(guests: List<Guest>)
    fun showHomeowners(homeowners: List<User>)
    fun showLoading()
    fun hideLoading()
    fun showError(message: String)
}
