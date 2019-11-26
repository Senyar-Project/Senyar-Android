package com.android.senyaar.Model

import android.util.Log
import com.android.senyaar.Model.generalResponse
import com.android.senyaar.Model.scheduledTripModel
import com.android.senyaar.NetworkCalls.JsonRequest
import com.android.senyaar.NetworkCalls.VolleyClassCentralized
import com.android.senyaar.Utils.Configuration
import com.android.senyaar.Utils.MyApplication
import com.android.senyaar.Utils.PreferenceHelper
import com.android.volley.Request
import com.google.gson.GsonBuilder

class ScheduledTripDataFeeder {

    private var requestGetRides: JsonRequest? = null
    lateinit var onFinished: OnFinishedListener

    interface OnFinishedListener {
        fun onResultSuccess(response: generalResponseArray)
        fun onResultFail(strError: String, tag: String)
    }

    fun getScheduledRides(onFinishedListener: OnFinishedListener, prefs: PreferenceHelper, url: String) {
        onFinished = onFinishedListener
        requestGetRides = JsonRequest(MyApplication.getInstance(), requestGetRidesListener)
        val params = HashMap<String, String>()
        //params["Content-Type"] = "application/json; charset=utf-8"
        params["Authorization"] = prefs.sign_in_token

        requestGetRides?.requestString(
            "get_driver_list",
            url,
            null,
            params,
            Request.Method.GET,
            false
        )
    }

    private val requestGetRidesListener =
        JsonRequest.OnRequestCompletedListener { requestName, status, response, errorMessage ->
            if (status) {
                Log.d("testing", "Response$response")
                val gsonBuilder = GsonBuilder()
                val gson = gsonBuilder.create()
                val responseModel = gson.fromJson(response, generalResponseArray::class.java)
                onFinished.onResultSuccess(responseModel)
            } else {
                onFinished.onResultFail(errorMessage, "getDriver")
            }
        }
}