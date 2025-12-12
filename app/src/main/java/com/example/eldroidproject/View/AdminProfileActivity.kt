package com.example.eldroidproject.View

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.eldroidproject.LoginActivity
import com.example.eldroidproject.Model.AuthRepository
import com.example.eldroidproject.Model.ProfileRepository
import com.example.eldroidproject.Presenter.ProfilePresenter
import com.example.eldroidproject.R

class AdminProfileActivity : Activity(), ProfileView.View {

    private lateinit var presenter: ProfilePresenter
    private var isEditing = false

    // UI Components
    private lateinit var tvDisplayEmail: TextView
    private lateinit var etPhone: EditText

    // Password Section
    private lateinit var changePasswordText: TextView
    private lateinit var passwordContainer: LinearLayout
    private lateinit var etNewPass: EditText
    private lateinit var etConfirmPass: EditText
    private lateinit var btnSavePassword: Button

    // Action Buttons
    private lateinit var btnEdit: Button
    private lateinit var btnLogout: Button

    // Navigation Buttons
    private lateinit var btnHome: View
    private lateinit var manageRequest: View
    private lateinit var btnProfile: View
    private lateinit var btnHistory: View // ✅ Added to fix crash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_profile)

        // 1. Initialize Presenter
        presenter = ProfilePresenter(this, ProfileRepository(), AuthRepository())

        // 2. Initialize Views (This prevents the crash)
        bindViews()

        // 3. Load Profile Data
        presenter.loadProfileData()

        // --- LISTENERS ---

        // Toggle Edit Mode
        btnEdit.setOnClickListener {
            if (!isEditing) {
                enableEditMode()
            } else {
                val phone = etPhone.text.toString().trim()
                // Admins don't show Plate/Model, so we pass empty strings
                presenter.saveProfileChanges(phone, "", "")
            }
        }

        // Password Logic
        changePasswordText.setOnClickListener { togglePasswordVisibility() }

        btnSavePassword.setOnClickListener {
            val newPass = etNewPass.text.toString()
            val confirmPass = etConfirmPass.text.toString()
            presenter.updatePassword(newPass, confirmPass)
        }

        // Logout
        btnLogout.setOnClickListener { presenter.onLogoutClicked() }

        // --- NAVIGATION ---
        setupNavigation()
    }

    private fun bindViews() {
        // Text & Inputs
        tvDisplayEmail = findViewById(R.id.tvDisplayEmail)
        etPhone = findViewById(R.id.profilePhone)

        // Password Views
        changePasswordText = findViewById(R.id.changePasswordText)
        passwordContainer = findViewById(R.id.passwordContainer)
        etNewPass = findViewById(R.id.profilePassword)
        etConfirmPass = findViewById(R.id.profilePasswordTwo)
        btnSavePassword = findViewById(R.id.btnSavePassword)

        // Main Buttons
        btnEdit = findViewById(R.id.btnEdit)
        btnLogout = findViewById(R.id.btnLogout)

        // Navigation Views (Must match IDs in XML)
        btnHome = findViewById(R.id.btnHome)
        manageRequest = findViewById(R.id.manageRequest)
        btnProfile = findViewById(R.id.btnProfile)
        btnHistory = findViewById(R.id.btnHistory) // ✅ Initialized here
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
            startActivity(Intent(this, AdminHistoryActivity::class.java))
            finish()
        }

        btnProfile.setOnClickListener {
            // Already on Profile
        }
    }

    private fun enableEditMode() {
        isEditing = true
        etPhone.isEnabled = true
        etPhone.requestFocus()

        btnEdit.text = "Save Changes"
        btnEdit.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, R.color.success_green)
        )
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

    // --- MVP Interface Methods ---

    override fun populateProfileFields(
        email: String,
        phone: String,
        plate: String,
        model: String,
        uuid: String
    ) {
        runOnUiThread {
            tvDisplayEmail.text = email
            etPhone.setText(phone)
        }
    }

    override fun onProfileSavedSuccess() {
        runOnUiThread {
            isEditing = false
            etPhone.isEnabled = false
            btnEdit.text = "Edit Profile"
            btnEdit.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.primary_blue)
            )
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
        }
    }

    override fun showMessage(message: String) {
        runOnUiThread { Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
    }

    override fun onPasswordUpdateSuccess() {
        runOnUiThread {
            Toast.makeText(this, "Password Updated", Toast.LENGTH_SHORT).show()
            togglePasswordVisibility()
        }
    }

    override fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // Unused Navigation Methods (Required by Interface)
    override fun navigateToHome() {}
    override fun navigateToGuestAccess() {}
    override fun navigateToHistory() {}
    override fun navigateToProfile() {}
}