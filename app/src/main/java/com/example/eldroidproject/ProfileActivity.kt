package com.example.eldroidproject

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.eldroidproject.Presenter.ProfilePresenter
import com.example.eldroidproject.View.ProfileView
import com.example.eldroidproject.Model.ProfileRepository
import com.example.eldroidproject.Model.AuthRepository

class ProfileActivity : Activity(), ProfileView.View {

    private lateinit var presenter: ProfileView.Presenter

    // --- State Variable ---
    private var isEditing = false

    // --- UI Components ---
    // Note: Removed tvDisplayName as requested
    private lateinit var tvDisplayEmail: TextView

    // Editable Fields
    private lateinit var etPhone: EditText
    private lateinit var etPlate: EditText
    private lateinit var etCarModel: EditText

    // Non-Editable Fields
    private lateinit var etUuid: EditText

    // Password Section
    private lateinit var changePasswordText: TextView
    private lateinit var passwordContainer: LinearLayout
    private lateinit var etNewPass: EditText
    private lateinit var etConfirmPass: EditText
    private lateinit var btnSavePassword: Button

    // Main Buttons
    private lateinit var btnEditProfile: Button
    private lateinit var btnLogout: Button

    // Bottom Nav
    private lateinit var homeButton: ImageButton
    private lateinit var guestAccessButton: ImageButton
    private lateinit var historyButton: ImageButton
    private lateinit var profileButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        presenter = ProfilePresenter(this, ProfileRepository(), AuthRepository())

        bindViews()
        setupNavigation()
        presenter.loadProfileData()

        // Ensure fields are locked on start
        setFieldsEnabled(false)

        // --- LISTENERS ---

        changePasswordText.setOnClickListener { togglePasswordVisibility() }

        btnSavePassword.setOnClickListener {
            val newPass = etNewPass.text.toString()
            val confirmPass = etConfirmPass.text.toString()
            presenter.updatePassword(newPass, confirmPass)
        }

        btnEditProfile.setOnClickListener {
            if (!isEditing) {
                // CASE 1: Switch to Edit Mode
                enableEditMode()
            } else {
                // CASE 2: Save Changes
                // Note: We removed 'name' and 'uuid' from this gathering logic
                val phone = etPhone.text.toString().trim()
                val plate = etPlate.text.toString().trim()
                val model = etCarModel.text.toString().trim()

                // ✅ FIXED: Only passing editable fields
                presenter.saveProfileChanges(phone, plate, model)
            }
        }

        btnLogout.setOnClickListener { presenter.onLogoutClicked() }
    }

    // --- Edit Mode Helpers ---

    private fun setFieldsEnabled(enabled: Boolean) {
        // ✅ ONLY these three fields are editable
        etPhone.isEnabled = enabled
        etPlate.isEnabled = enabled
        etCarModel.isEnabled = enabled

        // ❌ These remain locked even in edit mode
        etUuid.isEnabled = false
        // Email is a TextView, so it is naturally read-only
    }

    private fun enableEditMode() {
        isEditing = true
        setFieldsEnabled(true)

        btnEditProfile.text = "Save Changes"
        btnEditProfile.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, R.color.success_green)
        )

        etPhone.requestFocus()
    }

    // --- View Binding ---

    private fun bindViews() {
        // Removed tvDisplayName binding
        tvDisplayEmail = findViewById(R.id.tvDisplayEmail)

        etPhone = findViewById(R.id.profilePhone)
        etPlate = findViewById(R.id.profilePlate)
        etCarModel = findViewById(R.id.profileCar)
        etUuid = findViewById(R.id.bleUUID)

        changePasswordText = findViewById(R.id.changePasswordText)
        passwordContainer = findViewById(R.id.passwordContainer)
        etNewPass = findViewById(R.id.profilePassword)
        etConfirmPass = findViewById(R.id.profilePasswordTwo)
        btnSavePassword = findViewById(R.id.btnSavePassword)

        btnEditProfile = findViewById(R.id.btnEdit)
        btnLogout = findViewById(R.id.btnLogout)

        homeButton = findViewById(R.id.home)
        guestAccessButton = findViewById(R.id.guest_access)
        historyButton = findViewById(R.id.home_history)
        profileButton = findViewById(R.id.profile)
    }

    private fun togglePasswordVisibility() {
        if (passwordContainer.visibility == View.GONE) {
            passwordContainer.visibility = View.VISIBLE
            changePasswordText.text = "Cancel"
            changePasswordText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
        } else {
            passwordContainer.visibility = View.GONE
            changePasswordText.text = "Change"
            changePasswordText.setTextColor(ContextCompat.getColor(this, R.color.primary_blue))
            etNewPass.text.clear()
            etConfirmPass.text.clear()
        }
    }

    private fun setupNavigation() {
        homeButton.setOnClickListener { presenter.onHomeClicked() }
        guestAccessButton.setOnClickListener { presenter.onGuestAccessClicked() }
        historyButton.setOnClickListener { presenter.onHistoryClicked() }
        profileButton.setOnClickListener { presenter.onProfileClicked() }
    }

    // --- MVP Interface Implementation ---

    override fun populateProfileFields(
        email: String,
        phone: String,
        plate: String,
        model: String,
        uuid: String
    ) {
        runOnUiThread {
            // Removed name population
            tvDisplayEmail.text = email
            etPhone.setText(phone)
            etPlate.setText(plate)
            etCarModel.setText(model)
            etUuid.setText(uuid)
        }
    }

    override fun onProfileSavedSuccess() {
        runOnUiThread {
            isEditing = false
            setFieldsEnabled(false) // Lock fields

            btnEditProfile.text = "Edit Profile"
            btnEditProfile.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.primary_blue)
            )
        }
    }

    override fun showMessage(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPasswordUpdateSuccess() {
        runOnUiThread {
            Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show()
            togglePasswordVisibility()
        }
    }

    // --- Navigation ---

    override fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java)); finish()
    }
    override fun navigateToGuestAccess() {
        startActivity(Intent(this, GuestAccessActivity::class.java)); finish()
    }
    override fun navigateToHistory() {
        startActivity(Intent(this, HistoryActivity::class.java)); finish()
    }
    override fun navigateToProfile() { }
    override fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}