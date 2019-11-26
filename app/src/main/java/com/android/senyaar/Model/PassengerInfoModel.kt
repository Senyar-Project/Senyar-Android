package com.android.senyaar.Model

import android.util.Log
import com.android.senyaar.NetworkCalls.JsonRequest
import com.android.senyaar.Utils.Configuration
import com.android.senyaar.Utils.MyApplication
import com.android.senyaar.Utils.PreferenceHelper
import com.android.volley.Request
import org.json.JSONObject
import java.util.HashMap

class PassengerInfoModel {
    private var requestStartRide: JsonRequest? = null
    private var requestFareEstimate: JsonRequest? = null

    lateinit var onFinished: OnFinishedListener

    interface OnFinishedListener {
        fun onResultSuccess(response: String, tag: String)
        fun onResultFail(strError: String, tag: String)
    }

    fun startRide(onFinishedListener: OnFinishedListener, json: JSONObject, prefs: PreferenceHelper) {
        onFinished = onFinishedListener
        requestStartRide = JsonRequest(MyApplication.getInstance(), requestStartRideListener)
        val params = HashMap<String, String>()
        params["Content-Type"] = "application/json; charset=utf-8"
        params["Authorization"] = String.format("Bearer %s", prefs.sign_in_token)
        requestStartRide?.requestString(
            "start_ride",
            Configuration.ENDPOINT_START_RIDE,
            json,
            params,
            Request.Method.POST,
            false
        )
    }

    private val requestStartRideListener =
        JsonRequest.OnRequestCompletedListener { requestName, status, response, errorMessage ->
            if (status) {
                Log.d("testing", "Response$response")
                onFinished.onResultSuccess(response, "start")
            } else {
                onFinished.onResultFail(errorMessage, "start_ride")
            }
        }

    fun fareEstimate(
        onFinishedListener: OnFinishedListener,json: JSONObject, prefs: PreferenceHelper
    ) {
        onFinished = onFinishedListener
        requestFareEstimate = JsonRequest(MyApplication.getInstance(), requestStartRideListener)
        val params = HashMap<String, String>()
        params["Content-Type"] = "application/json; charset=utf-8"
        params["Authorization"] = String.format("Bearer %s", prefs.sign_in_token)
        requestFareEstimate?.requestString(
            "fare",
            Configuration.ENDPOINT_FARE_ESTIMATE,
            json,
            params,
            Request.Method.POST,
            false
        )
    }

}