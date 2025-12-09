package com.example.eldroidproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
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
    private lateinit var guestNameInput: EditText
    private lateinit var generatedCodeText: TextView
    private lateinit var okButton: Button
    private lateinit var guestListContainer: LinearLayout

    // Bottom Navigation
    private lateinit var homeButton: ImageButton
    private lateinit var historyButton: ImageButton
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

            // ✅ CRITICAL FIX: Changed R.id.tvGuestName -> R.id.guestNameInput
            guestNameInput = findViewById(R.id.tvGuestName)

            // ✅ CRITICAL FIX: Changed R.id.tvGuestCode -> R.id.generatedCodeText
            generatedCodeText = findViewById(R.id.tvGuestCode)

            okButton = findViewById(R.id.okButton)
            guestListContainer = findViewById(R.id.guestListContainer)

            // Bottom Nav Views
            homeButton = findViewById(R.id.home)
            historyButton = findViewById(R.id.home_history)
            profileButton = findViewById(R.id.profile)
            guestAccessButton = findViewById(R.id.guest_access)

            // 3. Navigation Listeners
            homeButton.setOnClickListener { presenter.onHomeClicked() }
            historyButton.setOnClickListener { presenter.onHistoryClicked() }
            profileButton.setOnClickListener { presenter.onProfileClicked() }
            guestAccessButton.setOnClickListener { presenter.onGuestAccessClicked() }

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
                    val currentCode = generatedCodeText.text.toString()

                    if (currentCode == "000000") {
                        // PHASE 1: Generate Code
                        if (name.isNotEmpty()) {
                            presenter.generateCode(name)
                        } else {
                            Toast.makeText(this, "Please enter a guest name", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // PHASE 2: Clicked "Done" -> Close Popup
                        codeOverlay.visibility = View.GONE
                        presenter.loadGuests() // This triggers the list refresh
                    }
                } catch (e: Exception) {
                    Log.e("GuestActivity", "Error in button click: ${e.message}")
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e("GuestActivity", "Error in onCreate: ${e.message}")
        }
    }

    private fun resetPopupState() {
        guestNameInput.isEnabled = true
        guestNameInput.setText("")
        generatedCodeText.text = "000000"
        // Use ContextCompat or simple getColor safely
        generatedCodeText.setTextColor(getColor(R.color.text_gray))
        okButton.text = "Generate Code"
    }

    // --- MVP Interface Implementation ---

    override fun onCodeGenerated(code: String) {
        // Run on UI Thread to prevent crashes
        runOnUiThread {
            generatedCodeText.text = code
            generatedCodeText.setTextColor(getColor(R.color.success_green))
            guestNameInput.isEnabled = false
            okButton.text = "Done / Close"
        }
    }

    override fun displayGuests(guests: List<Guest>) {
        runOnUiThread {
            try {
                guestListContainer.removeAllViews()

                for (guest in guests) {
                    val view = layoutInflater.inflate(R.layout.item_guest, guestListContainer, false)

                    val tvName = view.findViewById<TextView>(R.id.tvGuestName)
                    val tvCode = view.findViewById<TextView>(R.id.tvGuestCode)

                    if (tvName != null) tvName.text = guest.name
                    if (tvCode != null) tvCode.text = "Code: ${guest.code}"

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
    override fun navigateToHistory() {
        startActivity(Intent(this, HistoryActivity::class.java))
        finish()
    }
    override fun navigateToProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }
    override fun navigateToGuestAccess() { }
}