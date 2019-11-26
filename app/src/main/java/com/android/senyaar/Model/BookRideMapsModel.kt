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

class BookRideMapsModel {
    private var requestGetDrivers: JsonRequest? = null
    lateinit var onFinished: OnFinishedListener
    private var requestbookRide: JsonRequest? = null

    interface OnFinishedListener {
        fun onResultSuccess(response: String,tag:String)
        fun onResultFail(strError: String, tag: String)
    }

    fun getDrivers(onFinishedListener: OnFinishedListener, json: JSONObject, prefs: PreferenceHelper) {
        onFinished = onFinishedListener
        requestGetDrivers = JsonRequest(MyApplication.getInstance(), requestGetDriversListener)
        val params = HashMap<String, String>()
        params["Content-Type"] = "application/json; charset=utf-8"
       // params["Authorization"] = prefs.sign_in_token

        requestGetDrivers?.requestString(
            "get_driver",
            Configuration.ENDPOINT_DRIVERS_LIST,
            json,
            params,
            Request.Method.POST,
            false
        )
    }

    private val requestGetDriversListener =
        JsonRequest.OnRequestCompletedListener { requestName, status, response, errorMessage ->
            if (status) {
                Log.d("testing", "Response$response")

                onFinished.onResultSuccess(response,"get")
            } else {
                onFinished.onResultFail(errorMessage, "getDriver")
            }
        }

    fun bookRide(onFinishedListener: OnFinishedListener, json: JSONObject, prefs: PreferenceHelper) {
        onFinished = onFinishedListener
        requestbookRide = JsonRequest(MyApplication.getInstance(), requestbookRideListener)
        val params = HashMap<String, String>()
        params["Content-Type"] = "application/json; charset=utf-8"
        params["Authorization"] = String.format("Bearer %s",  prefs.sign_in_token)


        requestbookRide?.requestString(
            "book_ride",
            Configuration.ENDPOINT_BOOK_RIDE,
            json,
            params,
            Request.Method.POST,
            false
        )
    }

    private val requestbookRideListener =
        JsonRequest.OnRequestCompletedListener { requestName, status, response, errorMessage ->
            if (status) {
                Log.d("testing", "Response$response")
                onFinished.onResultSuccess(response,"book")
            } else {
                onFinished.onResultFail(errorMessage, "book_ride")
            }
        }
}