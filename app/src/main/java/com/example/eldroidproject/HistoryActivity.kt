package com.example.eldroidproject

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var historyContainer: LinearLayout
    private lateinit var homeButton: ImageButton
    private lateinit var guestAccessButton: ImageButton
    private lateinit var historyButton: ImageButton
    private lateinit var profileButton: ImageButton

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // ✅ Firebase reference to "History" node
        database = FirebaseDatabase.getInstance().getReference("History")

        // ✅ Initialize views
        historyContainer = findViewById(R.id.historyContainer)
        homeButton = findViewById(R.id.home)
        guestAccessButton = findViewById(R.id.guest_access)
        historyButton = findViewById(R.id.home_history)
        profileButton = findViewById(R.id.profile)

        // ✅ Navigation setup
        homeButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        guestAccessButton.setOnClickListener {
            startActivity(Intent(this, GuestAccessActivity::class.java))
            finish()
        }

        historyButton.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
            finish()
        }

        profileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }

        // ✅ Load and listen for updates in History node
        loadHistoryFromFirebase()
    }

    private fun loadHistoryFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                historyContainer.removeAllViews()

                if (!snapshot.exists()) {
                    val emptyText = TextView(this@HistoryActivity).apply {
                        text = "No history records yet."
                        textSize = 14f
                        gravity = Gravity.CENTER
                        setTextColor(Color.GRAY)
                    }
                    historyContainer.addView(emptyText)
                    return
                }

                // Display records (newest first)
                val records = snapshot.children.toList().reversed()
                for (recordSnap in records) {
                    val name = recordSnap.child("name").getValue(String::class.java) ?: "Unknown Guest"
                    val type = recordSnap.child("type").getValue(String::class.java) ?: "Unknown"
                    val vehicle = recordSnap.child("vehicle").getValue(String::class.java) ?: "N/A"
                    val timestamp = recordSnap.child("timestamp").getValue(String::class.java)
                        ?: SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                    val status = recordSnap.child("status").getValue(String::class.java) ?: "Active"

                    addHistoryCard(name, type, vehicle, timestamp, status)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@HistoryActivity,
                    "Failed to load history: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun addHistoryCard(
        name: String,
        type: String,
        vehicle: String,
        timestamp: String,
        status: String
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundResource(R.drawable.guest_card)
            setPadding(16, 16, 16, 16)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 12)
            layoutParams = params
        }

        val icon = ImageView(this).apply {
            setImageResource(R.drawable.key_logo)
            layoutParams = LinearLayout.LayoutParams(80, 80)
        }

        val infoLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            params.setMargins(16, 0, 0, 0)
            layoutParams = params
        }

        val titleText = TextView(this).apply {
            text = "$type - $status"
            textSize = 15f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(
                if (type.equals("Entry", ignoreCase = true))
                    Color.parseColor("#5CB85C")
                else
                    Color.parseColor("#D9534F")
            )
        }

        val nameText = TextView(this).apply {
            text = "Name: $name"
            textSize = 13f
            setTextColor(Color.BLACK)
        }

        val vehicleText = TextView(this).apply {
            text = "Vehicle: $vehicle"
            textSize = 13f
            setTextColor(Color.DKGRAY)
        }

        val dateText = TextView(this).apply {
            text = "Date: $timestamp"
            textSize = 12f
            setTextColor(Color.GRAY)
        }

        infoLayout.addView(titleText)
        infoLayout.addView(nameText)
        infoLayout.addView(vehicleText)
        infoLayout.addView(dateText)

        card.addView(icon)
        card.addView(infoLayout)

        historyContainer.addView(card)
    }
}
