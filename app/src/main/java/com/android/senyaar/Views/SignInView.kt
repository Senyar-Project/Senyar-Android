package com.android.senyaar.Views

import com.android.senyaar.Model.generalResponse

interface SignInView : BaseView {
    fun showSnackbar(message: String)
    fun loginSuccessful(response: generalResponse)
    fun getProfile(response: generalResponse)
    fun successfulLogin()
    fun changePassword(response: String)

}