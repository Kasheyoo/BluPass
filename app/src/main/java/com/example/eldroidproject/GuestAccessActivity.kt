package com.example.eldroidproject

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.eldroidproject.Model.Guest
import com.example.eldroidproject.Model.GuestRepository
import com.example.eldroidproject.Presenter.GuestAccessPresenter
import com.example.eldroidproject.View.GuestAccessView

class GuestAccessActivity : AppCompatActivity(), GuestAccessView.View {

    private lateinit var presenter: GuestAccessView.Presenter

    private lateinit var homeButton: ImageButton
    private lateinit var guestAccessButton: ImageButton
    private lateinit var historyButton: ImageButton
    private lateinit var profileButton: ImageButton
    private lateinit var guestContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_access)

        presenter = GuestAccessPresenter(this, GuestRepository())

        homeButton = findViewById(R.id.home)
        guestAccessButton = findViewById(R.id.guest_access)
        historyButton = findViewById(R.id.home_history)
        profileButton = findViewById(R.id.profile)
        guestContainer = findViewById(R.id.guestContainer)

        // 🔹 Navigation listeners
        homeButton.setOnClickListener { presenter.onHomeClicked() }
        guestAccessButton.setOnClickListener { presenter.onGuestAccessClicked() }
        historyButton.setOnClickListener { presenter.onHistoryClicked() }
        profileButton.setOnClickListener { presenter.onProfileClicked() }

        // 🔹 Load data
        presenter.loadGuests()
    }

    // ✅ Display guest list dynamically
    override fun displayGuests(guests: List<Guest>) {
        guestContainer.removeAllViews()
        for (guest in guests) {
            val textView = TextView(this)
            textView.text = "${guest.name} - ${guest.code} (${guest.status})"
            textView.textSize = 14f
            guestContainer.addView(textView)
        }
    }

    // ✅ Navigation logic
    override fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    override fun navigateToHistory() {
        startActivity(Intent(this, HistoryActivity::class.java))
        finish()
    }

    override fun navigateToProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }

    override fun navigateToGuestAccess() {
        // Stay on this screen
        startActivity(Intent(this, GuestAccessActivity::class.java))
        finish()
    }
}
