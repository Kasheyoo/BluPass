package com.example.eldroidproject.View

interface ProfileView {
    interface View {
        fun navigateToHome()
        fun navigateToGuestAccess()
        fun navigateToHistory()
        fun navigateToProfile()
        fun navigateToLogin()

        fun showMessage(message: String)
        fun onPasswordUpdateSuccess()
        fun onProfileSavedSuccess()

        // UUID remains here because we need to DISPLAY it
        fun populateProfileFields(
            email: String,
            phone: String,
            plate: String,
            model: String,
            uuid: String
        )
    }

    interface Presenter {
        fun onHomeClicked()
        fun onGuestAccessClicked()
        fun onHistoryClicked()
        fun onProfileClicked()
        fun onLogoutClicked()
        fun loadProfileData()

        // ✅ FIXED: Removed 'uuid' from here (User cannot edit UUID)
        fun saveProfileChanges(
            phone: String,
            plate: String,
            model: String
        )

        fun updatePassword(newPass: String, confirmPass: String)
    }
}