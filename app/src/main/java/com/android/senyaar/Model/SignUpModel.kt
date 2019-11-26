package com.android.senyaar.Model

import android.util.Log
import com.android.senyaar.NetworkCalls.JsonRequest
import com.android.senyaar.Utils.MyApplication
import com.android.senyaar.Utils.Configuration
import com.android.volley.Request
import com.google.gson.GsonBuilder
import org.json.JSONObject
import java.util.HashMap

class SignUpModel {
    private var requestSignUp: JsonRequest? = null
    lateinit var onFinished: OnFinishedListener

    interface OnFinishedListener {
        fun onResultSuccess(response: generalResponse)
        fun onResultFail(strError: String, tag: String)
    }

    fun signUp(onFinishedListener: OnFinishedListener, json: JSONObject) {
        onFinished = onFinishedListener
        requestSignUp = JsonRequest(MyApplication.getInstance(), requestSignInListener)
        val params = HashMap<String, String>()
        params["Content-Type"] = "application/json; charset=utf-8"

        requestSignUp?.requestString("sign_up", Configuration.ENDPOINT_SIGNUP, json, params, Request.Method.POST, false)
    }

    private val requestSignInListener =
        JsonRequest.OnRequestCompletedListener { requestName, status, response, errorMessage ->
            if (status) {
                val gsonBuilder = GsonBuilder()
                val gson = gsonBuilder.create()
                val responseModel = gson.fromJson(response, generalResponse::class.java)
                onFinished.onResultSuccess(responseModel)
                Log.d("jsonResponse", "Response Login$response")
            } else {
                onFinished.onResultFail(errorMessage, "signup")
            }
        }
}