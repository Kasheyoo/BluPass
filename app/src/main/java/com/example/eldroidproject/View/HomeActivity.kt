package com.example.eldroidproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.eldroidproject.Model.Gate
import com.example.eldroidproject.Model.GateRepository
import com.example.eldroidproject.Presenter.HomePresenter
import com.example.eldroidproject.View.HomeView

class HomeActivity : AppCompatActivity(), HomeView.View {

    private lateinit var presenter: HomeView.Presenter
    private lateinit var statusText: TextView
    private lateinit var openGateButton: Button
    private lateinit var historyButton: ImageButton
    private lateinit var profileButton: ImageButton
    private lateinit var homeButton: ImageButton
    private lateinit var guestAccessButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        presenter = HomePresenter(this, GateRepository())

        statusText = findViewById(R.id.statusText)
        openGateButton = findViewById(R.id.openGateButton)
        historyButton = findViewById(R.id.home_history)
        profileButton = findViewById(R.id.profile)
        homeButton = findViewById(R.id.home)
        guestAccessButton = findViewById(R.id.guest_access)

        presenter.loadGateStatus()

        // ✅ Navigation buttons
        homeButton.setOnClickListener { presenter.onHomeClicked() }
        historyButton.setOnClickListener { presenter.onHistoryClicked() }
        profileButton.setOnClickListener { presenter.onProfileClicked() }
        guestAccessButton.setOnClickListener { presenter.onGuestAccessClicked() }

        openGateButton.setOnClickListener { presenter.onOpenGateClicked() }
    }

    override fun displayGateStatus(gate: Gate) {
        statusText.text = "Status: ${gate.status}"
    }

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
        startActivity(Intent(this, GuestAccessActivity::class.java))
        finish()
    }
}
