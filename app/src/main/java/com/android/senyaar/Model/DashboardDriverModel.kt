package com.android.senyaar.Model

import android.util.Log
import com.android.senyaar.NetworkCalls.JsonRequest
import com.android.senyaar.Utils.Configuration
import com.android.senyaar.Utils.MyApplication
import com.android.senyaar.Utils.PreferenceHelper
import com.android.volley.Request
import com.google.gson.GsonBuilder
import org.json.JSONObject
import java.util.HashMap

class DashboardDriverModel {
    private var requestscheduleRide: JsonRequest? = null
    lateinit var onFinished: OnFinishedListener
    private var requestGetProfile: JsonRequest? = null

    interface OnFinishedListener {
        fun onResultSuccess(response: generalResponse, tag: String)
        fun onResultFail(strError: String, tag: String)
    }

    fun scheduleRide(onFinishedListener: OnFinishedListener, json: JSONObject, prefs: PreferenceHelper) {
        onFinished = onFinishedListener
        requestscheduleRide = JsonRequest(MyApplication.getInstance(), requestscheduleRideListener)
        val params = HashMap<String, String>()
        params["Content-Type"] = "application/json; charset=utf-8"
        params["Authorization"] = prefs.sign_in_token

        requestscheduleRide?.requestString(
            "schedule_driver",
            Configuration.ENDPOINT_SCHEDULE_DRIVER,
            json,
            params,
            Request.Method.POST,
            false
        )
    }

    private val requestscheduleRideListener =
        JsonRequest.OnRequestCompletedListener { requestName, status, response, errorMessage ->
            if (status) {
                Log.d("testing", "Response$response")
                val gsonBuilder = GsonBuilder()
                val gson = gsonBuilder.create()
                val responseModel = gson.fromJson(response, generalResponse::class.java)
                onFinished.onResultSuccess(responseModel, "schedule")
            } else {
                onFinished.onResultFail(errorMessage, "getDriver")
            }
        }

    fun getProfile(onFinishedListener: OnFinishedListener, prefs: PreferenceHelper) {
        onFinished = onFinishedListener
        requestGetProfile = JsonRequest(MyApplication.getInstance(), requestGetProfileListener)
        val params = HashMap<String, String>()
        params["Content-Type"] = "application/json; charset=utf-8"
        params["Authorization"] = prefs.sign_in_token

        requestGetProfile?.requestString(
            "get_profile",
            Configuration.ENDPOINT_GET_PROFILE,
            null,
            params,
            Request.Method.GET,
            false
        )
    }

    private val requestGetProfileListener =
        JsonRequest.OnRequestCompletedListener { requestName, status, response, errorMessage ->
            if (status) {
                Log.d("testing", "Response$response")
                val gsonBuilder = GsonBuilder()
                val gson = gsonBuilder.create()
                val responseModel = gson.fromJson(response, generalResponse::class.java)
                onFinished.onResultSuccess(responseModel, "getProfile")
            } else {
                onFinished.onResultFail(errorMessage, "getProfile")
            }
        }
}