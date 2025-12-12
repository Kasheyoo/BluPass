    package com.example.eldroidproject.Presenter

    import com.example.eldroidproject.Model.GuestRepository
    import com.example.eldroidproject.View.GuestAccessView

    class GuestAccessPresenter(
        private val view: GuestAccessView.View,
        private val repository: GuestRepository
    ) : GuestAccessView.Presenter {

        override fun generateCode(guestName: String, vehicle: String) {
            val randomCode = (100000..999999).random().toString()

            repository.saveGuestInvite(guestName, vehicle, randomCode) { success, message ->
                if (success) {
                    view.onCodeGenerated(randomCode)
                } else {
                    view.showError(message ?: "Error generating code")
                }
            }
        }

        override fun loadGuests() {
            // Calls Function 2 from Repository
            repository.getGuestList { guests ->
                view.displayGuests(guests)
            }
        }

        // Navigation
        override fun onHomeClicked() { view.navigateToHome() }
        override fun onHistoryClicked() { view.navigateToHistory() }
        override fun onProfileClicked() { view.navigateToProfile() }
        override fun onGuestAccessClicked() { view.navigateToGuestAccess() }
    }