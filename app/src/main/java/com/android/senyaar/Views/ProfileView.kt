package com.android.senyaar.Views

interface ProfileView : BaseView {
    fun showSnackbar(message: String)
    fun getProfile(response: String)
    fun updateProfile(response: String)

}