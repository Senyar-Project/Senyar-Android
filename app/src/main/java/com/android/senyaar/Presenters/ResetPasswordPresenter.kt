package com.android.senyaar.Presenters

import com.android.senyaar.Model.ResetPasswordModel
import com.android.senyaar.Utils.PreferenceHelper
import com.android.senyaar.Views.SignInView
import org.json.JSONObject

class ResetPasswordPresenter(private var view: SignInView, var model: ResetPasswordModel) :
    ResetPasswordModel.OnFinishedListener {
    init {
        view.initView()
    }

    override fun onResultSuccess(response: String, tag: String) {
        view.hideProgress()
        view.changePassword(response)

    }

    override fun onResultFail(strError: String, tag: String) {
        view.hideProgress()
        view.showServerError(strError, tag)
    }

    fun changePassword(
        old_password: String, new_password: String,
        prefs: PreferenceHelper
    ) {
        var change_password = JSONObject()
        change_password.put("old_password", old_password)
        change_password.put("new_password", new_password)
        view.showProgress()
        model.chnagePassword(this, change_password, prefs)
    }


    fun verifyFields(old_password: String, new_password: String, confirm_password: String, prefs: PreferenceHelper) {
        if (old_password.isEmpty()) {
            view.showSnackbar("Enter previous password")
            return
        }
        if (old_password.length < 8) {
            view.showSnackbar("Password must be at least 8 characters long")
            return
        }
        if (new_password.isEmpty()) {
            view.showSnackbar("Enter new password")
            return
        }
        if (new_password.length < 8) {
            view.showSnackbar("Password must be at least 8 characters long")
            return
        }
        if (confirm_password.isEmpty()) {
            view.showSnackbar("Confirm password")
            return
        }
        if (confirm_password.length < 8) {
            view.showSnackbar("Password must be at least 8 characters long")
            return
        }
        if (new_password != confirm_password) {
            view.showSnackbar("Passwords do not match")
            return
        } else {
            changePassword(old_password, new_password, prefs)
        }
    }
}