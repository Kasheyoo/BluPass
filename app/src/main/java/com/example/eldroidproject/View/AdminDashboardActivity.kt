package com.example.eldroidproject.View

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.eldroidproject.Model.AdminRepository // ✅ Import Repository
import com.example.eldroidproject.Model.Guest
import com.example.eldroidproject.Model.User
import com.example.eldroidproject.Presenter.AdminDashboardPresenter
import com.example.eldroidproject.ProfileActivity
import com.example.eldroidproject.R

class AdminDashboardActivity : Activity(), AdminDashboardView {

    private lateinit var presenter: AdminDashboardPresenter

    // Containers
    private lateinit var homeownersContainer: LinearLayout
    private lateinit var guestsContainer: LinearLayout

    // Stats Views
    private lateinit var tvHomeownerCount: TextView
    private lateinit var tvPendingCount: TextView

    // Navigation
    private lateinit var btnHome: View
    private lateinit var manageRequest: View
    private lateinit var btnProfile: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // 1. Initialize Views
        homeownersContainer = findViewById(R.id.homeownersContainer)
        guestsContainer = findViewById(R.id.guestsContainer)

        tvHomeownerCount = findViewById(R.id.tvHomeownerCount)
        tvPendingCount = findViewById(R.id.tvPendingCount)

        btnHome = findViewById(R.id.btnHome)
        manageRequest = findViewById(R.id.manageRequest)
        btnProfile = findViewById(R.id.btnProfile)

        // 2. ✅ FIXED: Pass AdminRepository to the Presenter
        presenter = AdminDashboardPresenter(this, AdminRepository())

        // 3. Load Data
        presenter.loadDashboard()

        // 4. Setup Navigation
        setupNavigation()
    }

    private fun setupNavigation() {
        btnHome.setOnClickListener { /* Already here */ }

        manageRequest.setOnClickListener {
            startActivity(Intent(this, AdminManageHomeownersActivity::class.java))
            // Keep finish() if you want to close dashboard, otherwise remove it
            finish()
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, AdminProfileActivity::class.java))
            // Don't finish() here to allow back navigation
        }
    }

    // --- View Interface Implementation ---

    override fun showHomeowners(homeowners: List<User>) {
        runOnUiThread {
            tvHomeownerCount.text = homeowners.size.toString()
            homeownersContainer.removeAllViews()
            val inflater = LayoutInflater.from(this)

            for (homeowner in homeowners) {
                val card = inflater.inflate(R.layout.item_homeowner, homeownersContainer, false)

                val tvName = card.findViewById<TextView>(R.id.tvHomeownerName)
                val tvDetails = card.findViewById<TextView>(R.id.tvHomeownerDetails)

                // Data Binding
                tvName.text = homeowner.email.ifEmpty { "Unknown User" }

                val lotData = homeowner.lotNumber
                val lotDisplay = if (!lotData.isNullOrEmpty() && lotData != "null") "Lot: $lotData" else "No Lot"

                val mobileData = homeowner.mobile
                val mobileDisplay = if (!mobileData.isNullOrEmpty() && mobileData != "null") mobileData else "No Phone"

                tvDetails.text = "$lotDisplay • $mobileDisplay"

                homeownersContainer.addView(card)
            }
        }
    }

    override fun showGuests(guests: List<Guest>) {
        runOnUiThread {
            guestsContainer.removeAllViews()
            val inflater = LayoutInflater.from(this)

            for (guest in guests) {
                val card = inflater.inflate(R.layout.item_guest_admin, guestsContainer, false)

                val tvName = card.findViewById<TextView>(R.id.tvGuestName)
                val tvCode = card.findViewById<TextView>(R.id.tvGuestCode)

                tvName.text = guest.name.ifEmpty { "Unnamed Guest" }
                tvCode.text = "Code: ${guest.code}"

                guestsContainer.addView(card)
            }
        }
    }

    override fun showLoading() {
        runOnUiThread { Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show() }
    }

    override fun hideLoading() { }

    override fun showError(message: String) {
        runOnUiThread { Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
    }
}