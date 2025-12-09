package com.example.eldroidproject.Presenter

import com.example.eldroidproject.Model.AuthRepository
import com.example.eldroidproject.Model.ProfileRepository
import com.example.eldroidproject.View.ProfileView

class ProfilePresenter(
    private val view: ProfileView.View,
    private val profileRepo: ProfileRepository,
    private val authRepo: AuthRepository
) : ProfileView.Presenter {

    override fun onHomeClicked() = view.navigateToHome()
    override fun onGuestAccessClicked() = view.navigateToGuestAccess()
    override fun onHistoryClicked() = view.navigateToHistory()
    override fun onProfileClicked() = view.navigateToProfile()

    override fun onLogoutClicked() {
        authRepo.logoutUser()
        view.navigateToLogin()
    }

    override fun loadProfileData() {
        profileRepo.getProfileData(
            onSuccess = { profile ->
                view.populateProfileFields(
                    email = profile.email,
                    phone = profile.phone,
                    plate = profile.plateNumber,
                    model = profile.carModel,
                    uuid = profile.uuid
                )
            },
            onFailure = { error ->
                view.showMessage("Failed to load profile: $error")
            }
        )
    }

    // ✅ FIXED: Removed 'uuid' parameter
    override fun saveProfileChanges(
        phone: String,
        plate: String,
        model: String
    ) {
        if (phone.isBlank()) {
            view.showMessage("Phone number cannot be empty")
            return
        }

        // ✅ FIXED: Removed 'uuid' from this call.
        // Now arguments match (String, String, String, Function, Function)
        profileRepo.saveProfileData(
            phone, plate, model,
            onSuccess = {
                view.showMessage("Profile updated successfully!")
                view.onProfileSavedSuccess()
                loadProfileData()
            },
            onFailure = { error -> view.showMessage("Error: $error") }
        )
    }

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