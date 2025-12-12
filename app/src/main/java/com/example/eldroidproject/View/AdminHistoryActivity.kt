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
import com.example.eldroidproject.ProfileActivity
import com.example.eldroidproject.R

class AdminHistoryActivity : Activity() {

    private lateinit var historyContainer: LinearLayout
    private val repository = AdminRepository()

    // Navigation
    private lateinit var btnHome: View
    private lateinit var manageRequest: View
    private lateinit var btnHistory: View
    private lateinit var btnProfile: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_history) // See layout below

        historyContainer = findViewById(R.id.historyContainer)

        // Navigation Init
        btnHome = findViewById(R.id.btnHome)
        manageRequest = findViewById(R.id.manageRequest)
        btnHistory = findViewById(R.id.btnHistory)
        btnProfile = findViewById(R.id.btnProfile)

        setupNavigation()
        loadHistory()
    }

    private fun loadHistory() {
        repository.fetchGateHistory(
            onSuccess = { logs ->
                historyContainer.removeAllViews()
                val inflater = LayoutInflater.from(this)

                if (logs.isEmpty()) {
                    val emptyView = TextView(this)
                    emptyView.text = "No history records found."
                    emptyView.setPadding(32, 32, 32, 32)
                    historyContainer.addView(emptyView)
                    return@fetchGateHistory
                }

                for (log in logs) {
                    val card = inflater.inflate(R.layout.item_admin_history, historyContainer, false)

                    val tvOpenedBy = card.findViewById<TextView>(R.id.tvOpenedBy)
                    val tvTime = card.findViewById<TextView>(R.id.tvTimestamp)

                    tvOpenedBy.text = log.openedBy
                    tvTime.text = log.timestamp

                    historyContainer.addView(card)
                }
            },
            onFailure = {
                Toast.makeText(this, "Error: $it", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun setupNavigation() {
        btnHome.setOnClickListener {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
            finish()
        }
        manageRequest.setOnClickListener {
            startActivity(Intent(this, AdminManageHomeownersActivity::class.java))
            finish()
        }
        btnHistory.setOnClickListener {
            // Already here
        }
        btnProfile.setOnClickListener {
            startActivity(Intent(this, AdminProfileActivity::class.java))
            finish()
        }
    }
}