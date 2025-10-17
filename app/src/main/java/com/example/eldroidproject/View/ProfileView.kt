package com.example.eldroidproject.View

interface ProfileView {
    interface View {
        fun navigateToHome()
        fun navigateToGuestAccess()
        fun navigateToHistory()
        fun navigateToProfile()
        fun navigateToLogin()
        fun showMessage(message: String)

        fun populateProfileFields(name: String, email: String, phone: String, plate: String, model: String)  // ✅ Add this

    }

    interface Presenter {
        fun onHomeClicked()
        fun onGuestAccessClicked()
        fun onHistoryClicked()
        fun onProfileClicked()
        fun onLogoutClicked()
        fun saveProfileChanges(name: String, email: String, phone: String, plate: String, model: String)
        fun onChangePasswordClicked(newPassword: String)   // ✅ added

        fun loadProfileData()

    }
}
