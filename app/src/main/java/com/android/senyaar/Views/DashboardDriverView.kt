package com.android.senyaar.Views

import com.android.senyaar.Model.generalResponse

interface DashboardDriverView : BaseView {
    fun showSnackbar(message: String)
    fun scheduleRide(response: generalResponse)
    fun getProfile(response: generalResponse)

}