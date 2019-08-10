package com.android.senyaar.Views

interface SignInView:BaseView {
    fun showSnackbar(message: String)
    fun successfulLogin()
}