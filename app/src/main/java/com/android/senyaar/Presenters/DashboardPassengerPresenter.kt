package com.android.senyaar.Presenters

import android.util.Log
import com.android.senyaar.Model.ProfileModel
import com.android.senyaar.Utils.PreferenceHelper
import com.android.senyaar.Views.DashboardPassengerView

class DashboardPassengerPresenter(private var view: DashboardPassengerView, var model: ProfileModel):
    ProfileModel.OnFinishedListener {
    init {
        view.initView()
    }

    fun verifyFields(leavingFrom: String, goingTo: String, departureTime: String, departureDate: String,journeyType:String): Boolean {
        if (leavingFrom == "Leave from") {
            view.showSnackbar("Select your pick up location")
            return false
        }
        if (goingTo == "Going to") {
            view.showSnackbar("Select your drop off location")
            return false
        }
        if (departureTime == "Departure Time") {
            view.showSnackbar("Select your departure time")
            return false
        }
        if (departureDate == "Departure Date") {
            view.showSnackbar("Select your departure date")
            return false
        }
        if (journeyType == "Journey type") {
            view.showSnackbar("Select your journey type")
            return false
        }else {
            return true
        }
    }
    override fun onResultSuccess(response: String, tag: String) {
        view.hideProgress()
        view.getProfile(response)
    }

    override fun onResultFail(strError: String, tag: String) {
        view.hideProgress()
        view.showServerError(strError, tag)
    }


    fun getProfile(
        prefs: PreferenceHelper
    ) {
        Log.d("testing","Get profile")
        model.getProfile(this, prefs)
    }
}