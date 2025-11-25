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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

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
    private lateinit var etUUID: EditText

    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var isEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        presenter = ProfilePresenter(this, ProfileRepository(), AuthRepository())

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("Users")

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

        // ✅ Automatically retrieve and display profile data
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userRef = databaseRef.child(currentUser.uid)

            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name = snapshot.child("username").getValue(String::class.java) ?: ""
                        val email = snapshot.child("email").getValue(String::class.java) ?: ""
                        val phone = snapshot.child("phone").getValue(String::class.java) ?: ""
                        val plate = snapshot.child("plate").getValue(String::class.java) ?: ""
                        val model = snapshot.child("model").getValue(String::class.java) ?: ""

                        // Populate UI
                        populateProfileFields(name, email, phone, plate, model)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showMessage("Failed to load profile: ${error.message}")
                }
            })
        }

        // 🔹 Edit button
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

        // 🔹 Password section
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

        // Navigation
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
