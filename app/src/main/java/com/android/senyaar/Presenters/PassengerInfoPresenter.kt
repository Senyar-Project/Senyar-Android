package com.android.senyaar.Presenters

import com.android.senyaar.Model.PassengerInfoModel
import com.android.senyaar.Utils.PreferenceHelper
import com.android.senyaar.Views.PassengerInfoView
import org.json.JSONObject

class PassengerInfoPresenter(private var view: PassengerInfoView, var model: PassengerInfoModel) :
    PassengerInfoModel.OnFinishedListener {
    init {
        view.initView()
    }

    override fun onResultSuccess(response: String, tag: String) {
        view.hideProgress()
        //if (tag.equals("start")) {
        view.startRide(response)
        //}

    }

    override fun onResultFail(strError: String, tag: String) {
        view.hideProgress()
        view.showServerError(strError, tag)
    }


    fun startRide(
        start_ride_json: JSONObject,
        prefs: PreferenceHelper
    ) {
        view.showProgress()
        model.startRide(this, start_ride_json, prefs)
    }

    fun fareEstimate(
        fare_estimate_json: JSONObject,
        prefs: PreferenceHelper
    ) {
        view.showProgress()
        model.fareEstimate(this, fare_estimate_json, prefs)

    }

}