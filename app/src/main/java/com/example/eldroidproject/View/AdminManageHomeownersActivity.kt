package com.example.eldroidproject.View

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.eldroidproject.Model.AdminRepository
import com.example.eldroidproject.Model.User
import com.example.eldroidproject.Presenter.AdminManagePresenter
import com.example.eldroidproject.ProfileActivity
import com.example.eldroidproject.R
import com.google.android.material.button.MaterialButton

class AdminManageHomeownersActivity : Activity(), AdminManageView {

    private lateinit var presenter: AdminManagePresenter
    private lateinit var pendingContainer: LinearLayout // ✅ Logic matches XML now

    // Navigation
    private lateinit var btnHome: View
    private lateinit var manageRequest: View
    private lateinit var btnProfile: View
    private lateinit var btnHistory: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_manage_homeowners)

        // 1. Initialize Views
        pendingContainer = findViewById(R.id.pendingContainer)

        btnHome = findViewById(R.id.btnHome)
        manageRequest = findViewById(R.id.manageRequest)
        btnProfile = findViewById(R.id.btnProfile)
        btnHistory = findViewById(R.id.btnHistory)

        // 2. Initialize Presenter
        presenter = AdminManagePresenter(this, AdminRepository())

        // 3. Load Data
        presenter.loadPendingUsers()

        // 4. Setup Navigation
        setupNavigation()
    }

    private fun setupNavigation() {
        btnHome.setOnClickListener {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
            finish()
        }

        btnHistory.setOnClickListener {
            startActivity(Intent(this, AdminHistoryActivity::class.java))
            finish()
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, AdminProfileActivity::class.java))
        }
    }

    // --- View Interface Implementation ---

    override fun showPendingList(users: List<User>) {
        runOnUiThread {
            pendingContainer.removeAllViews()
            val inflater = LayoutInflater.from(this)

            if (users.isEmpty()) {
                val emptyView = TextView(this)
                emptyView.text = "No pending requests."
                emptyView.setPadding(32, 32, 32, 32)
                pendingContainer.addView(emptyView)
                return@runOnUiThread
            }

            for (user in users) {
                val card = inflater.inflate(R.layout.item_homeowner_request, pendingContainer, false)

                val tvName = card.findViewById<TextView>(R.id.tvPendingName)
                val tvDetails = card.findViewById<TextView>(R.id.tvPendingDetails)
                val btnApprove = card.findViewById<MaterialButton>(R.id.btnConfirmPending)
                val btnReject = card.findViewById<MaterialButton>(R.id.btnRejectPending)

                // Bind Data
                tvName.text = user.email.ifEmpty { "Unknown User" }
                val lotInfo = if (user.lotNumber.isNullOrEmpty()) "No Lot" else user.lotNumber
                tvDetails.text = "$lotInfo"

                // --- CHANGED LOGIC BELOW ---
                // We now use 'user.email' as the key to find and update the user

                btnApprove.setOnClickListener {
                    if (user.email.isNotEmpty()) {
                        presenter.approveUser(user.email) // Pass email instead of UID
                    } else {
                        showMessage("Error: User Email missing")
                    }
                }

                btnReject.setOnClickListener {
                    if (user.email.isNotEmpty()) {
                        presenter.rejectUser(user.email) // Pass email instead of UID
                    } else {
                        showMessage("Error: User Email missing")
                    }
                }

                pendingContainer.addView(card)
            }
        }
    }

    override fun refreshList() {
        presenter.loadPendingUsers()
    }

    override fun showMessage(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}