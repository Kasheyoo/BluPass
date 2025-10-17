package com.example.eldroidproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.eldroidproject.Model.AuthRepository
import com.example.eldroidproject.Model.ProfileRepository
import com.example.eldroidproject.Presenter.ProfilePresenter
import com.example.eldroidproject.View.ProfileView

class ProfileActivity : AppCompatActivity(), ProfileView.View {

    private lateinit var presenter: ProfileView.Presenter
    private lateinit var btnEdit: Button
    private lateinit var btnLogout: Button
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etPlate: EditText
    private lateinit var etModel: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSavePassword: Button
    private lateinit var passwordContainer: LinearLayout
    private lateinit var changePasswordText: TextView

    private lateinit var homeButton: ImageButton
    private lateinit var guestAccessButton: ImageButton
    private lateinit var historyButton: ImageButton
    private lateinit var profileButton: ImageButton

    private var isEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        presenter = ProfilePresenter(this, ProfileRepository(), AuthRepository())

        // Initialize views
        btnEdit = findViewById(R.id.btnEdit)
        btnLogout = findViewById(R.id.btnLogout)
        etName = findViewById(R.id.profileName)
        etEmail = findViewById(R.id.profileEmail)
        etPhone = findViewById(R.id.profilePhone)
        etPlate = findViewById(R.id.profilePlate)
        etModel = findViewById(R.id.profileCar)

        passwordContainer = findViewById(R.id.passwordContainer)
        etPassword = findViewById(R.id.profilePassword)
        btnSavePassword = findViewById(R.id.btnSavePassword)
        changePasswordText = findViewById(R.id.changePasswordText)
        passwordContainer.visibility = View.GONE

        homeButton = findViewById(R.id.home)
        guestAccessButton = findViewById(R.id.guest_access)
        historyButton = findViewById(R.id.home_history)
        profileButton = findViewById(R.id.profile)

        setEditingEnabled(false)

        // 🔹 Load latest user data when profile opens
        presenter.loadProfileData()

        // 🔹 Edit button (toggle save)
        btnEdit.setOnClickListener {
            if (!isEditing) {
                isEditing = true
                setEditingEnabled(true)
                btnEdit.text = "Save"
            } else {
                isEditing = false
                setEditingEnabled(false)
                btnEdit.text = "Edit"
                presenter.saveProfileChanges(
                    etName.text.toString(),
                    etEmail.text.toString(),
                    etPhone.text.toString(),
                    etPlate.text.toString(),
                    etModel.text.toString()
                )
            }
        }

        // 🔹 Password change section
        changePasswordText.setOnClickListener {
            passwordContainer.visibility = View.VISIBLE
        }

        btnSavePassword.setOnClickListener {
            val newPassword = etPassword.text.toString().trim()
            if (newPassword.isEmpty()) {
                showMessage("Please enter a new password.")
                return@setOnClickListener
            }
            if (newPassword.length < 6) {
                showMessage("Password must be at least 6 characters.")
                return@setOnClickListener
            }

            presenter.onChangePasswordClicked(newPassword)
            etPassword.text.clear()
            passwordContainer.visibility = View.GONE
        }

        btnLogout.setOnClickListener { presenter.onLogoutClicked() }

        // 🔹 Navigation buttons
        homeButton.setOnClickListener { presenter.onHomeClicked() }
        guestAccessButton.setOnClickListener { presenter.onGuestAccessClicked() }
        historyButton.setOnClickListener { presenter.onHistoryClicked() }
        profileButton.setOnClickListener { presenter.onProfileClicked() }
    }

    private fun setEditingEnabled(enabled: Boolean) {
        etName.isEnabled = enabled
        etEmail.isEnabled = enabled
        etPhone.isEnabled = enabled
        etPlate.isEnabled = enabled
        etModel.isEnabled = enabled
    }

    // ✅ Populate UI with latest Firebase data
    override fun populateProfileFields(
        name: String,
        email: String,
        phone: String,
        plate: String,
        model: String
    ) {
        etName.setText(name)
        etEmail.setText(email)
        etPhone.setText(phone)
        etPlate.setText(plate)
        etModel.setText(model)
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Navigation
    override fun navigateToHome() = navigateTo(HomeActivity::class.java)
    override fun navigateToGuestAccess() = navigateTo(GuestAccessActivity::class.java)
    override fun navigateToHistory() = navigateTo(HistoryActivity::class.java)
    override fun navigateToProfile() = navigateTo(ProfileActivity::class.java)
    override fun navigateToLogin() = navigateTo(LoginActivity::class.java)

    private fun navigateTo(target: Class<*>) {
        startActivity(Intent(this, target))
        finish()
    }
}
