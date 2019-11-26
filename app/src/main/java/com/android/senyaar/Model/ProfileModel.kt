package com.android.senyaar.Model

import android.util.Log
import com.android.senyaar.NetworkCalls.JsonRequest
import com.android.senyaar.Utils.Configuration
import com.android.senyaar.Utils.MyApplication
import com.android.senyaar.Utils.PreferenceHelper
import com.android.volley.Request
import org.json.JSONObject

class ProfileModel {
    private var requestGetProfile: JsonRequest? = null
    lateinit var onFinished: OnFinishedListener
    private var requestUpdateProfile: JsonRequest? = null

    interface OnFinishedListener {
        fun onResultSuccess(response: String,tag:String)
        fun onResultFail(strError: String, tag: String)
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

                onFinished.onResultSuccess(response,"get")
            } else {
                onFinished.onResultFail(errorMessage, "getProfile")
            }
        }
    fun updateProfile(onFinishedListener: OnFinishedListener, json: JSONObject, prefs: PreferenceHelper) {
        onFinished = onFinishedListener
        requestUpdateProfile = JsonRequest(MyApplication.getInstance(), requestUpdateProfileListener)
        val params = HashMap<String, String>()
        params["Content-Type"] = "application/json; charset=utf-8"
        params["Authorization"] = String.format("Bearer %s",  prefs.sign_in_token)


        requestUpdateProfile?.requestString(
            "update_profile",
            Configuration.ENDPOINT_UPDATE_PROFILE,
            json,
            params,
            Request.Method.POST,
            false
        )
    }

    private val requestUpdateProfileListener =
        JsonRequest.OnRequestCompletedListener { requestName, status, response, errorMessage ->
            if (status) {
                Log.d("testing", "Response$response")
                onFinished.onResultSuccess(response,"book")
            } else {
                onFinished.onResultFail(errorMessage, "book_ride")
            }
        }
}