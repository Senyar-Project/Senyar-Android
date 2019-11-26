package com.android.senyaar.Model

import android.util.Log
import com.android.senyaar.NetworkCalls.JsonRequest
import com.android.senyaar.Utils.MyApplication
import com.android.senyaar.Utils.Configuration
import com.android.senyaar.Utils.PreferenceHelper
import com.android.volley.Request
import com.google.gson.GsonBuilder
import org.json.JSONObject
import java.util.HashMap

class SignInModel {
    private var requestSignIn: JsonRequest? = null
    lateinit var onFinished: OnFinishedListener
    private var requestGetProfile: JsonRequest? = null

    interface OnFinishedListener {
        fun onResultSuccess(response: generalResponse, tag: String)
        fun onResultFail(strError: String, tag: String)
    }

    fun login(onFinishedListener: OnFinishedListener, uname: String, passw: String) {
        onFinished = onFinishedListener
        requestSignIn = JsonRequest(MyApplication.getInstance(), requestSignInListener)
        val params = HashMap<String, String>()
        params["Content-Type"] = "application/json; charset=utf-8"
        var signin_json = JSONObject()
        signin_json.put("username", uname.toLowerCase())
        signin_json.put("password", passw)
        requestSignIn?.requestString(
            "sign_in",
            Configuration.ENDPOINT_LOGIN,
            signin_json,
            params,
            Request.Method.POST,
            false
        )
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
                onFinished.onResultSuccess(responseModel, "get")
            } else {
                onFinished.onResultFail(errorMessage, "getProfile")
            }
        }
    private val requestSignInListener =
        JsonRequest.OnRequestCompletedListener { requestName, status, response, errorMessage ->
            if (status) {
                val gsonBuilder = GsonBuilder()
                val gson = gsonBuilder.create()
                val responseModel = gson.fromJson(response, generalResponse::class.java)
                onFinished.onResultSuccess(responseModel, "login")
                Log.d("jsonResponse", "Response Login$response")
            } else {
                onFinished.onResultFail(errorMessage, "login")
            }
        }
}