package com.example.eldroidproject.View

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.eldroidproject.Model.User
import com.example.eldroidproject.R
import com.google.firebase.database.*

class AdminManageHomeownersActivity : Activity() {

    private lateinit var pendingContainer: LinearLayout
    private lateinit var homeownersContainer: LinearLayout
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_manage_homeowners)

        pendingContainer = findViewById(R.id.pendingContainer)
        homeownersContainer = findViewById(R.id.homeownersContainer)

        database = FirebaseDatabase.getInstance().getReference("users")

        loadUsers()
    }

    private fun loadUsers() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear old views
                pendingContainer.removeAllViews()
                homeownersContainer.removeAllViews()

                for (userSnap in snapshot.children) {
                    val user = userSnap.getValue(User::class.java)
                    val uid = userSnap.key ?: continue

                    if (user != null) {
                        if (user.status == "pending") {
                            addPendingUserView(user, uid)
                        } else if (user.status == "approved") {
                            addApprovedUserView(user)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminManageHomeownersActivity, "Failed to load users", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addPendingUserView(user: User, uid: String) {
        val inflater = LayoutInflater.from(this)
        val row = inflater.inflate(R.layout.item_pending_user, pendingContainer, false)

        val tvName = row.findViewById<TextView>(R.id.tvPendingName)
        val btnConfirm = row.findViewById<Button>(R.id.btnConfirmPending)

        tvName.text = user.username

        btnConfirm.setOnClickListener {
            database.child(uid).child("status").setValue("approved")
                .addOnSuccessListener {
                    Toast.makeText(this, "${user.username} approved!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to approve ${user.username}", Toast.LENGTH_SHORT).show()
                }
        }

        pendingContainer.addView(row)
    }

    private fun addApprovedUserView(user: User) {
        val inflater = LayoutInflater.from(this)
        val row = inflater.inflate(R.layout.item_homeowner, homeownersContainer, false)

        val tvName = row.findViewById<TextView>(R.id.tvHomeownerName)
        tvName.text = user.username

        homeownersContainer.addView(row)
    }
}