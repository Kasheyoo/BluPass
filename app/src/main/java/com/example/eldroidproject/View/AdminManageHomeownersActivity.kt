package com.example.eldroidproject.View

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.eldroidproject.Model.User
import com.example.eldroidproject.R
import com.google.firebase.database.*

class AdminManageHomeownersActivity : Activity() {

    private lateinit var pendingContainer: LinearLayout
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_manage_homeowners)

        val home = findViewById<ImageButton>(R.id.btnHome)
        home.setOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            startActivity(intent)
        }

        pendingContainer = findViewById(R.id.pendingContainer)

        database = FirebaseDatabase.getInstance().getReference("users/homeowners")

        loadPendingHomeowners()
    }

    private fun loadPendingHomeowners() {
        database.orderByChild("status").equalTo("pending")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pendingContainer.removeAllViews()

                    for (homeSnap in snapshot.children) {
                        val user = homeSnap.getValue(User::class.java)
                        val uid = homeSnap.key ?: continue

                        if (user != null) {
                            addPendingUserView(user, uid)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@AdminManageHomeownersActivity,
                        "Failed to load pending homeowners: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
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
}
