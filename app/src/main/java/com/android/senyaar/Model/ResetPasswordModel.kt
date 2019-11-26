package com.android.senyaar.Model

import android.util.Log
import com.android.senyaar.NetworkCalls.JsonRequest
import com.android.senyaar.Utils.Configuration
import com.android.senyaar.Utils.MyApplication
import com.android.senyaar.Utils.PreferenceHelper
import com.android.volley.Request
import org.json.JSONObject

class ResetPasswordModel {
    private var requestResetPassword: JsonRequest? = null
    lateinit var onFinished: OnFinishedListener

    interface OnFinishedListener {
        fun onResultSuccess(response: String,tag:String)
        fun onResultFail(strError: String, tag: String)
    }

    fun chnagePassword(onFinishedListener: OnFinishedListener, json: JSONObject, prefs: PreferenceHelper) {
        onFinished = onFinishedListener
        requestResetPassword = JsonRequest(MyApplication.getInstance(), requestResetPasswordListener)
        val params = HashMap<String, String>()
        params["Content-Type"] = "application/json; charset=utf-8"
        params["Authorization"] = prefs.sign_in_token

        requestResetPassword?.requestString(
            "reset_password",
            Configuration.ENDPOINT_RESET_PASSWORD,
            json,
            params,
            Request.Method.POST,
            false
        )
    }

    private val requestResetPasswordListener =
        JsonRequest.OnRequestCompletedListener { requestName, status, response, errorMessage ->
            if (status) {
                Log.d("testing", "Response$response")
                onFinished.onResultSuccess(response,"get")
            } else {
                onFinished.onResultFail(errorMessage, "getProfile")
            }
        }
}