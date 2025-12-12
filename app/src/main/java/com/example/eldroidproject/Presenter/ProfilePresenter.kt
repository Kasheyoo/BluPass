package com.example.eldroidproject.Presenter

import com.example.eldroidproject.Model.AuthRepository
import com.example.eldroidproject.Model.ProfileRepository
import com.example.eldroidproject.View.ProfileView

class ProfilePresenter(
    private val view: ProfileView.View,
    private val profileRepo: ProfileRepository,
    private val authRepo: AuthRepository
) : ProfileView.Presenter {

    // --- Navigation ---
    override fun onHomeClicked() = view.navigateToHome()
    override fun onGuestAccessClicked() = view.navigateToGuestAccess()
    override fun onHistoryClicked() = view.navigateToHistory()
    override fun onProfileClicked() = view.navigateToProfile()

    override fun onLogoutClicked() {
        authRepo.logoutUser() // Ensure AuthRepository has this method (or use logout())
        view.navigateToLogin()
    }

    // --- Data Loading ---
    override fun loadProfileData() {
        // This calls the updated Repository method which:
        // 1. Fetches User Details (Phone, Plate, Model)
        // 2. Queries 'registeredDevices' to find the matching 'userUUID'
        // 3. Returns the correct Advertised UUID
        profileRepo.getProfileData(
            onSuccess = { profile ->
                view.populateProfileFields(
                    email = profile.email,
                    phone = profile.phone,
                    plate = profile.plateNumber,
                    model = profile.carModel,
                    uuid = profile.uuid // ✅ Correctly populated from the Repo Query
                )
            },
            onFailure = { error ->
                view.showMessage("Failed to load profile: $error")
            }
        )
    }

    // --- Saving Changes ---
    override fun saveProfileChanges(
        phone: String,
        plate: String,
        model: String
    ) {
        if (phone.isBlank()) {
            view.showMessage("Phone number cannot be empty")
            return
        }

        // We do NOT save the UUID here because it is hardware-dependent and read-only.
        profileRepo.saveProfileData(
            phone, plate, model,
            onSuccess = {
                view.showMessage("Profile updated successfully!")
                view.onProfileSavedSuccess()
                loadProfileData() // Refresh data to confirm changes
            },
            onFailure = { error -> view.showMessage("Error: $error") }
        )
    }

    // --- Password Update ---
    override fun updatePassword(newPass: String, confirmPass: String) {
        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            view.showMessage("Please enter both password fields")
            return
        }
        if (newPass != confirmPass) {
            view.showMessage("Passwords do not match")
            return
        }
        if (newPass.length < 6) {
            view.showMessage("Password must be at least 6 characters")
            return
        }
        profileRepo.updatePassword(
            newPass,
            onSuccess = { view.onPasswordUpdateSuccess() },
            onFailure = { error -> view.showMessage("Failed to update password: $error") }
        )
    }
}