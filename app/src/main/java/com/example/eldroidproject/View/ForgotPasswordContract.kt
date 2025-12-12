package com.example.eldroidproject.View

interface ForgotPasswordContract {

    interface View {
        fun showLoading(isLoading: Boolean)
        fun showEmailError(message: String?)
        fun showMessage(message: String)
        fun showSuccess(message: String)
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun onSendCodeClicked(email: String)
    }
}
