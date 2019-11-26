package com.android.senyaar.Views

import com.android.senyaar.Model.generalResponse

interface SignUpView:BaseView {
    fun showSnackbar(message: String)
    fun signUpSuccessful(response: generalResponse)

}