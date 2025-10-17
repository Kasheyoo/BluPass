package com.example.eldroidproject.Model

class GuestRepository {

    private val guestList = listOf(
        Guest("Sarah Wilson", "A09-0926", "Active"),
        Guest("Mark Johnson", "A09-0925", "Used"),
        Guest("Lisa Chen", "A09-0924", "Expired")
    )

    fun getGuestList(): List<Guest> {
        return guestList
    }
}
