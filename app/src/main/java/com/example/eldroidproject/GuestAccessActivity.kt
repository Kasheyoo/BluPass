package com.example.eldroidproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.eldroidproject.Model.Guest
import com.example.eldroidproject.Model.GuestRepository
import com.example.eldroidproject.Presenter.GuestAccessPresenter
import com.example.eldroidproject.View.GuestAccessView

class GuestAccessActivity : Activity(), GuestAccessView.View {

    private lateinit var presenter: GuestAccessView.Presenter

    // --- UI Components ---
    private lateinit var inviteButton: Button
    private lateinit var codeOverlay: FrameLayout
    private lateinit var codeCard: CardView

    // Popup Inputs
    private lateinit var guestNameInput: EditText
    private lateinit var guestVehicleInput: EditText // ✅ Added Vehicle Input
    private lateinit var generatedCodeText: TextView
    private lateinit var okButton: Button

    private lateinit var guestListContainer: LinearLayout

    // Bottom Navigation (History Removed)
    private lateinit var homeButton: ImageButton
    private lateinit var profileButton: ImageButton
    private lateinit var guestAccessButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_access)

        try {
            // 1. Initialize Presenter
            presenter = GuestAccessPresenter(this, GuestRepository())

            // 2. Initialize Views
            inviteButton = findViewById(R.id.inviteButton)
            codeOverlay = findViewById(R.id.codeOverlay)
            codeCard = findViewById(R.id.codeCard)
            guestListContainer = findViewById(R.id.guestListContainer)

            // ✅ Popup Views (IDs matched to your XML)
            guestNameInput = findViewById(R.id.tvGuestName)
            guestVehicleInput = findViewById(R.id.tvGuestVehicle) // ✅ Bind Vehicle Input
            generatedCodeText = findViewById(R.id.tvGuestCode)
            okButton = findViewById(R.id.okButton)

            // Bottom Nav Views
            homeButton = findViewById(R.id.home)
            profileButton = findViewById(R.id.profile)
            guestAccessButton = findViewById(R.id.guest_access)

            // 3. Navigation Listeners
            homeButton.setOnClickListener { presenter.onHomeClicked() }
            profileButton.setOnClickListener { presenter.onProfileClicked() }
            guestAccessButton.setOnClickListener { /* Already here */ }

            // 4. Load Initial Data
            presenter.loadGuests()

            // --- POPUP LOGIC ---

            // A. Open Popup
            inviteButton.setOnClickListener {
                resetPopupState()
                codeOverlay.visibility = View.VISIBLE
            }

            // B. Close Popup
            codeOverlay.setOnClickListener {
                codeOverlay.visibility = View.GONE
            }
            codeCard.setOnClickListener { /* Consume click */ }

            // C. Generate / Close Logic
            okButton.setOnClickListener {
                try {
                    val name = guestNameInput.text.toString().trim()
                    val vehicle = guestVehicleInput.text.toString().trim() // ✅ Get Vehicle
                    val currentCode = generatedCodeText.text.toString()

                    if (currentCode == "000000") {
                        // PHASE 1: Generate Code
                        if (name.isNotEmpty()) {
                            // ✅ Pass Name AND Vehicle to Presenter
                            presenter.generateCode(name, vehicle)
                        } else {
                            Toast.makeText(this, "Please enter a guest name", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // PHASE 2: Done -> Close Popup & Refresh
                        codeOverlay.visibility = View.GONE
                        presenter.loadGuests()
                    }
                } catch (e: Exception) {
                    Log.e("GuestActivity", "Error in button click: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("GuestActivity", "Error in onCreate: ${e.message}")
        }
    }

    private fun resetPopupState() {
        guestNameInput.isEnabled = true
        guestNameInput.setText("")

        // ✅ Reset Vehicle Input
        guestVehicleInput.isEnabled = true
        guestVehicleInput.setText("")

        generatedCodeText.text = "000000"
        generatedCodeText.setTextColor(ContextCompat.getColor(this, R.color.text_gray))
        okButton.text = "Generate Code"
    }

    // --- MVP Interface Implementation ---

    override fun onCodeGenerated(code: String) {
        runOnUiThread {
            generatedCodeText.text = code
            generatedCodeText.setTextColor(ContextCompat.getColor(this, R.color.success_green))

            // ✅ Lock fields after generation
            guestNameInput.isEnabled = false
            guestVehicleInput.isEnabled = false

            okButton.text = "Done / Close"
        }
    }

    override fun displayGuests(guests: List<Guest>) {
        runOnUiThread {
            try {
                guestListContainer.removeAllViews()

                if (guests.isEmpty()) {
                    val emptyView = TextView(this)
                    emptyView.text = "No active guest keys."
                    emptyView.setPadding(16, 32, 16, 16)
                    emptyView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    emptyView.setTextColor(ContextCompat.getColor(this, R.color.text_gray))
                    guestListContainer.addView(emptyView)
                    return@runOnUiThread
                }

                for (guest in guests) {
                    val view = layoutInflater.inflate(R.layout.item_guest, guestListContainer, false)

                    val tvName = view.findViewById<TextView>(R.id.tvGuestName)
                    val tvCode = view.findViewById<TextView>(R.id.tvGuestCode)

                    if (tvName != null) {
                        // ✅ Display Name AND Vehicle (if exists)
                        val displayName = if (guest.vehicle.isNotEmpty()) "${guest.name} (${guest.vehicle})" else guest.name
                        tvName.text = displayName
                    }

                    if (tvCode != null) {
                        tvCode.text = "Code: ${guest.code}"
                    }

                    guestListContainer.addView(view)
                }
            } catch (e: Exception) {
                Log.e("GuestActivity", "Error displaying guests: ${e.message}")
            }
        }
    }

    override fun showError(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    // --- Navigation ---
    override fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
    override fun navigateToProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }

    override fun navigateToHistory() { }
    override fun navigateToGuestAccess() { }
}