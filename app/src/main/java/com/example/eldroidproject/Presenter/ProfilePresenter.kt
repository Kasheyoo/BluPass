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

    override fun saveProfileChanges(name: String, email: String, phone: String, plate: String, model: String) {
        profileRepo.saveProfileData(
            name, email, phone, plate, model,
            onSuccess = { view.showMessage("Profile updated successfully!") },
            onFailure = { error -> view.showMessage("Error: $error") }
        )
    }

    override fun onChangePasswordClicked(newPassword: String) {
        profileRepo.updatePassword(
            newPassword,
            onSuccess = {
                view.showMessage("Password updated successfully! Please use it next time you log in.")
            },
            onFailure = { error ->
                view.showMessage("Failed to update password: $error")
            }
        )
    }

    override fun loadProfileData() {
        profileRepo.getProfileData(
            onSuccess = { data ->
                view.populateProfileFields(
                    name = data["username"] ?: "",
                    email = data["email"] ?: "",
                    phone = data["phone"] ?: "",
                    plate = data["plate"] ?: "",
                    model = data["model"] ?: ""
                )
            },
            onFailure = { error ->
                view.showMessage("Failed to load profile: $error")
            }
        )
    }


}
