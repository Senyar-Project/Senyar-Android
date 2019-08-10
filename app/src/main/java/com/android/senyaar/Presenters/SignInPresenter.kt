package com.android.senyaar.Presenters

import com.android.senyaar.Views.SignInView

class SignInPresenter(private var view: SignInView) {
    init {
        view.initView()
    }

    fun verifyFields(email: String, password: String) {
        if (email.isEmpty()) {
            view.showSnackbar("Enter email address")
            return
        }
        if (!emailVerification(email.replace(" ", ""))) {
            view.showSnackbar("Email address is not valid")
            return
        }
        if (password.isEmpty()) {
            view.showSnackbar("Enter password")
            return
        }
        if (password.length < 8) {
            view.showSnackbar("Password must be at least 8 characters long")
            return
        } else {
            view.successfulLogin()
        }
    }

    fun emailVerification(email: String?): Boolean {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}