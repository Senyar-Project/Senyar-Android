package com.android.senyaar.Presenters

import com.android.senyaar.Model.SignInModel
import com.android.senyaar.Model.generalResponse
import com.android.senyaar.Utils.CommonMethods
import com.android.senyaar.Utils.PreferenceHelper
import com.android.senyaar.Views.SignInView
import java.time.format.SignStyle

class SignInPresenter(private var view: SignInView, var model: SignInModel) : SignInModel.OnFinishedListener {
    override fun onResultSuccess(response: generalResponse, tag: String) {
        view.hideProgress()
        if (tag.equals("get")) {
            view.getProfile(response)
        } else {
            view.loginSuccessful(response)
        }
    }

    override fun onResultFail(strError: String, tag: String) {
        view.hideProgress()
        view.showServerError(strError, tag)
    }

    init {
        view.initView()
    }

    fun verifyFields(email: String, password: String) {
        if (email.isEmpty()) {
            view.showSnackbar("Enter email address")
            return
        }
        if (!CommonMethods.emailVerification(email.replace(" ", ""))) {
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
            login(email, password)
        }
    }

    fun login(email: String, password: String) {
        view.showProgress()
        model.login(this, email, password)
    }

    fun getProfile(
        prefs: PreferenceHelper
    ) {
        view.showProgress()
        model.getProfile(this, prefs)
    }
}