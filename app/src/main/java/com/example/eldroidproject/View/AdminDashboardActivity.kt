package com.example.eldroidproject.View

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.eldroidproject.Model.AdminRepository
import com.example.eldroidproject.Model.Guest
import com.example.eldroidproject.Model.User
import com.example.eldroidproject.Presenter.AdminDashboardPresenter
import com.example.eldroidproject.R
import com.google.android.material.button.MaterialButton

class AdminDashboardActivity : Activity(), AdminDashboardView {

    private lateinit var presenter: AdminDashboardPresenter

    // Containers
    private lateinit var homeownersContainer: LinearLayout
    private lateinit var guestsContainer: LinearLayout

    // Stats Views
    private lateinit var tvHomeownerCount: TextView
    private lateinit var tvPendingCount: TextView

    // Navigation & Buttons
    private lateinit var btnHome: View
    private lateinit var manageRequest: View
    private lateinit var btnProfile: View
    private lateinit var btnHistory: View
    private lateinit var btnOpenGate: MaterialButton

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
        btnHistory = findViewById(R.id.btnHistory)

        btnOpenGate = findViewById(R.id.btnOpenGate)

        // 2. Initialize Presenter
        presenter = AdminDashboardPresenter(this, AdminRepository())

        // 3. Load Data
        presenter.loadDashboard()

        // 4. Setup Listeners
        setupNavigation()
        setupOpenGate()
    }

    private fun setupNavigation() {
        btnHome.setOnClickListener { /* Already here */ }

        manageRequest.setOnClickListener {
            startActivity(Intent(this, AdminManageHomeownersActivity::class.java))
            finish()
        }

        btnHistory.setOnClickListener {
            startActivity(Intent(this, AdminHistoryActivity::class.java))
            finish()
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, AdminProfileActivity::class.java))
            finish()
        }
    }

    private fun setupOpenGate() {
        btnOpenGate.setOnClickListener {
            // ✅ Call the presenter logic
            presenter.openGate()
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

                // 1. Display Name + Vehicle
                val vehicleInfo = if (guest.vehicle.isNotEmpty()) " (${guest.vehicle})" else ""
                tvName.text = "${guest.name}$vehicleInfo"

                // 2. Format Status
                val rawStatus = guest.status.ifEmpty { "Inactive" }
                val formattedStatus = rawStatus.replaceFirstChar { it.uppercase() }

                // 3. Display Lot + Code + Status
                val lotDisplay = if (guest.lotNumber.isNotEmpty()) "Unit ${guest.lotNumber} • " else ""
                tvCode.text = "${lotDisplay}Code: ${guest.code} • $formattedStatus"

                // 4. Status Color
                if (rawStatus.equals("active", ignoreCase = true)) {
                    tvCode.setTextColor(Color.parseColor("#10B981")) // Green
                } else {
                    tvCode.setTextColor(Color.parseColor("#9CA3AF")) // Gray
                }

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