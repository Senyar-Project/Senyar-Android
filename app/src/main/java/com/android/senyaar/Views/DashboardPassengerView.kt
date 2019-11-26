package com.android.senyaar.Views

import com.android.senyaar.Model.generalResponse
import com.android.senyaar.Model.generalResponseArray

interface DashboardPassengerView: BaseView {
    fun showSnackbar(message: String)
    fun getProfile(response: String)
}