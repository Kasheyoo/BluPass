package com.example.eldroidproject.View

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.eldroidproject.Model.User
import com.example.eldroidproject.R
import com.google.firebase.database.*

class AdminDashboardActivity : Activity() {

    private lateinit var homeownersContainer: LinearLayout
    private lateinit var guestsContainer: LinearLayout
    private lateinit var database: DatabaseReference
    private lateinit var databaseG: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val manageRequest = findViewById<ImageButton>(R.id.manageRequest)
        manageRequest.setOnClickListener {
            val intent = Intent(this, AdminManageHomeownersActivity::class.java)
            startActivity(intent)
        }

        homeownersContainer = findViewById(R.id.homeownersContainer)
        guestsContainer = findViewById(R.id.guestsContainer)

        database = FirebaseDatabase.getInstance().getReference("users")
        databaseG = FirebaseDatabase.getInstance().getReference("Guest")

        loadApprovedHomeowners()
        loadGuests()
    }

    /** ✅ Load approved homeowners */
    private fun loadApprovedHomeowners() {
        database.child("homeowners").orderByChild("status").equalTo("approved")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    homeownersContainer.removeAllViews()

                    for (homeSnap in snapshot.children) {
                        val user = homeSnap.getValue(User::class.java)
                        if (user != null) {
                            addApprovedUserView(user)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@AdminDashboardActivity,
                        "Failed to load homeowners: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    /** ✅ Load all guests from Guest node */
    private fun loadGuests() {
        val guestRef = FirebaseDatabase.getInstance().getReference("Guest")
        guestRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                guestsContainer.removeAllViews()

                for (guestSnap in snapshot.children) {
                    val name = guestSnap.child("name").getValue(String::class.java) ?: "Unknown"
                    val code = guestSnap.child("code").getValue(String::class.java) ?: "N/A"
                    val status = guestSnap.child("status").getValue(String::class.java) ?: "Active"

                    addGuestAccessView(name, code, status)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@AdminDashboardActivity,
                    "Failed to load guests: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    /** ✅ Render homeowner row */
    private fun addApprovedUserView(user: User) {
        val inflater = LayoutInflater.from(this)
        val row = inflater.inflate(R.layout.item_homeowner, homeownersContainer, false)

        val tvName = row.findViewById<TextView>(R.id.tvHomeownerName)
        tvName.text = user.username

        homeownersContainer.addView(row)
    }

    private fun addGuestAccessView(name: String, code: String, status: String) {
        val inflater = LayoutInflater.from(this)
        val row = inflater.inflate(R.layout.item_guest, guestsContainer, false)

        val tvName = row.findViewById<TextView>(R.id.tvGuestName)
        val tvAccessCode = row.findViewById<TextView>(R.id.tvGuestAccessCode)
        val tvStatus = row.findViewById<TextView>(R.id.tvGuestStatus)

        tvName.text = name
        tvAccessCode.text = "Code: $code"
        tvStatus.text = "Status: $status"

        guestsContainer.addView(row)
    }
}
