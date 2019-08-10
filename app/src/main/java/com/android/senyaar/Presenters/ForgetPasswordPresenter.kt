package com.android.senyaar.Presenters

import com.android.senyaar.Views.BaseView
import com.android.senyaar.Views.SignInView

class ForgetPasswordPresenter(private var view: SignInView) {
    init {
        view.initView()
    }

    fun verifyFields(email: String) {
        if (email.isEmpty()) {
            view.showSnackbar("Enter email address")
            return
        }
        if (!emailVerification(email.replace(" ", ""))) {
            view.showSnackbar("Email address is not valid")
            return
        }
    }

    fun emailVerification(email: String?): Boolean {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}