package com.example.eldroidproject

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class GuestAccessActivity : AppCompatActivity() {

    private lateinit var inviteButton: Button
    private lateinit var guestListContainer: LinearLayout
    private lateinit var codeOverlay: LinearLayout
    private lateinit var generatedCodeText: TextView
    private lateinit var okButton: Button
    private lateinit var guestNameInput: EditText

    private lateinit var homeButton: ImageButton
    private lateinit var guestAccessButton: ImageButton
    private lateinit var historyButton: ImageButton
    private lateinit var profileButton: ImageButton

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_access)

        // 🔹 Firebase reference
        database = FirebaseDatabase.getInstance().getReference("Guest")

        // 🔹 Initialize UI components
        inviteButton = findViewById(R.id.inviteButton)
        guestListContainer = findViewById(R.id.guestListContainer)
        codeOverlay = findViewById(R.id.codeOverlay)
        generatedCodeText = findViewById(R.id.generatedCodeText)
        okButton = findViewById(R.id.okButton)
        guestNameInput = findViewById(R.id.guestNameInput)

        homeButton = findViewById(R.id.home)
        guestAccessButton = findViewById(R.id.guest_access)
        historyButton = findViewById(R.id.home_history)
        profileButton = findViewById(R.id.profile)

        // 🔹 Navigation setup
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

        // 🔹 Show overlay and generate random code
        inviteButton.setOnClickListener {
            codeOverlay.visibility = View.VISIBLE
            val code = (100000..999999).random().toString()
            generatedCodeText.text = code
        }

        // 🔹 Save guest to Firebase
        // 🔹 Save guest to Firebase
        okButton.setOnClickListener {
            val guestName = guestNameInput.text.toString().trim()
            val code = generatedCodeText.text.toString()

            if (guestName.isEmpty()) {
                Toast.makeText(this, "Please enter guest name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val guestData = mapOf(
                "name" to guestName,
                "code" to code,
                "status" to "Active"
            )

            val guestId = database.push().key
            if (guestId != null) {
                database.child(guestId).setValue(guestData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Guest saved to Firebase", Toast.LENGTH_SHORT).show()
                        addGuestCard(guestName, code, "Active")

                        // ✅ NEW: Save this event to "History"
                        val historyRef = FirebaseDatabase.getInstance().getReference("History")
                        val historyId = historyRef.push().key
                        val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())

                        val historyRecord = mapOf(
                            "name" to guestName,
                            "type" to "Entry", // You can change this to "Invite" or "Code Generated"
                            "vehicle" to "N/A",
                            "timestamp" to timestamp,
                            "status" to "Active"
                        )

                        if (historyId != null) {
                            historyRef.child(historyId).setValue(historyRecord)
                        }

                        // ✅ Reset overlay
                        guestNameInput.text.clear()
                        codeOverlay.visibility = View.GONE
                        codeOverlay.isClickable = false
                        codeOverlay.isFocusable = false
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }


        // 🔹 Load guests on app start
        loadGuestsFromFirebase()
    }

    // ✅ Loads all guests from Firebase
    private fun loadGuestsFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                guestListContainer.removeAllViews() // clear before reloading
                for (guestSnapshot in snapshot.children) {
                    val name = guestSnapshot.child("name").getValue(String::class.java) ?: "Unknown"
                    val code = guestSnapshot.child("code").getValue(String::class.java) ?: "N/A"
                    val status = guestSnapshot.child("status").getValue(String::class.java) ?: "Active"
                    addGuestCard(name, code, status)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@GuestAccessActivity,
                    "Failed to load guests: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // ✅ Adds a guest card dynamically to UI
    private fun addGuestCard(name: String, code: String, status: String) {
        val guestCard = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundResource(R.drawable.guest_card)
            setPadding(12, 12, 12, 12)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 8)
            layoutParams = params
        }

        val initials = name.split(" ")
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .joinToString("")
        val avatar = TextView(this).apply {
            text = initials.take(2)
            textSize = 16f
            setTextColor(android.graphics.Color.WHITE)
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
            width = 100
            height = 100
            setBackgroundResource(R.drawable.circle_avatar)
        }

        val infoLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            params.setMargins(16, 0, 0, 0)
            layoutParams = params
        }

        val nameText = TextView(this).apply {
            text = name
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val codeText = TextView(this).apply {
            text = "Code: $code"
            textSize = 12f
            setTextColor(android.graphics.Color.DKGRAY)
        }

        val statusText = TextView(this).apply {
            text = "Status: $status"
            textSize = 12f
            setTextColor(android.graphics.Color.parseColor("#5CB85C"))
        }

        infoLayout.addView(nameText)
        infoLayout.addView(codeText)
        infoLayout.addView(statusText)

        guestCard.addView(avatar)
        guestCard.addView(infoLayout)

        guestListContainer.addView(guestCard)
    }
}
