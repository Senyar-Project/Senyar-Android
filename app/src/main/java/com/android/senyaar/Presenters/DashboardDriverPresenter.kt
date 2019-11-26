package com.android.senyaar.Presenters

import android.util.Log
import com.android.senyaar.Model.DashboardDriverModel
import com.android.senyaar.Model.generalResponse
import com.android.senyaar.Utils.PreferenceHelper
import com.android.senyaar.Views.DashboardDriverView
import org.json.JSONObject

class DashboardDriverPresenter(private var view: DashboardDriverView, var model: DashboardDriverModel) :
    DashboardDriverModel.OnFinishedListener {
    init {
        view.initView()
    }

    override fun onResultSuccess(response: generalResponse, tag: String) {
        view.hideProgress()
        if (tag.equals("schedule")) {
            view.scheduleRide(response)
        } else {
            view.getProfile(response)
        }
    }

    override fun onResultFail(strError: String, tag: String) {
        view.hideProgress()
        view.showServerError(strError, tag)
    }

    fun verifyFields(leavingFrom: String, goingTo: String, departureTime: String, departureDate: String): Boolean {
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
        } else {
            return true
        }
    }


    fun scheduleRide(
        leavingFromLatitude: String,
        leavingFromLongitude: String,
        leavingFromName: String,
        goingToLatitude: String,
        goingToLongitude: String,
        goingToName: String,
        departureDate: String,
        departureTime: String,
        prefs: PreferenceHelper
    ) {
        view.showProgress()
        var schedule_json = JSONObject()
        schedule_json.put("schedule_date", departureDate)
        schedule_json.put("schedule_time", departureTime)
        schedule_json.put("schedule_start_lat", leavingFromLatitude)
        schedule_json.put("schedule_start_long", leavingFromLongitude)
        schedule_json.put("schedule_start_location_name", leavingFromName)
        schedule_json.put("schedule_end_lat", goingToLatitude)
        schedule_json.put("schedule_end_long", goingToLongitude)
        schedule_json.put("schedule_end_location_name", goingToName)
        model.scheduleRide(this, schedule_json, prefs)
    }

    fun getProfile(
        prefs: PreferenceHelper
    ) {
        model.getProfile(this, prefs)
    }
}