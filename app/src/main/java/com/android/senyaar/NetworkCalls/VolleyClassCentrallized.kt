package com.android.senyaar.NetworkCalls

import android.content.Context
import android.text.TextUtils
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.util.HashMap

class VolleyClassCentrallized {
    // private static final String TAG = Logger.makeLogTag(VolleyRequestHelper.class);
    private val context: Context
    private var requestQueue: RequestQueue?=null
    private var mRequestCompletedListener: OnRequestCompletedListener?=null

    constructor(context: Context) {
        this.context = context
    }

    constructor(context: Context, callback: OnRequestCompletedListener) {
        mRequestCompletedListener = callback
        this.context = context
    }

    fun requestString(
        requestName: String,
        webserviceUrl: String,
        requestParams: HashMap<String, String>, headerParams: HashMap<String, String>,
        webMethod: Int,
        getCache: Boolean
    ) {
        val stringRequest = object : StringRequest(webMethod,
            webserviceUrl, Response.Listener<String> { response ->
                mRequestCompletedListener?.onRequestCompleted(
                    requestName, true, response, null
                )
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    var errorResponse = ""
                    // Log.d("testing","Code"+error.networkResponse.statusCode+"DATA: "+error.networkResponse.data);
                    if (error.networkResponse != null) {
                        // if (error.networkResponse.statusCode == 400 || error.networkResponse.statusCode == 406 || error.networkResponse.statusCode == 401 || error.networkResponse.statusCode == 500){
                        try {
                            val body = String(error.networkResponse.data, StandardCharsets.UTF_8)
                            val obj = JSONObject(body)
                            if (obj.has("message")) {
                                if (obj.getString("message").isEmpty()) {
                                    errorResponse = "Server error.Please try again."
                                } else {
                                    errorResponse = obj.getString("message")
                                }
                            } else if (obj.has("data")) {
                                if (obj.getString("data") != null) {
                                    errorResponse = obj.getString("data")
                                }
                            }
                        } catch (e: UnsupportedEncodingException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else if (error is TimeoutError) {
                        errorResponse = "Connection Timed out.Please try again."
                    } else if (error is NoConnectionError) {
                        errorResponse = "No internet connection. Please try again."
                    } else if (error is ParseError) {
                        errorResponse = "Parse error occurred. Please try again."
                    } else if (error is NetworkError) {
                        errorResponse = "No network. Please try again."
                    } else if (error is AuthFailureError) {
                        errorResponse = "Invalid email or password."
                    } else if (error is ServerError) {
                        errorResponse = "Server error. Please try again."
                    } else {
                        errorResponse = error.toString()
                    }
                    mRequestCompletedListener?.onRequestCompleted(
                        requestName, false, null,
                        errorResponse
                    )
                }
            }) {
            /* @Override
       public String getBodyContentType() {
       return Configuration.APPLICATION_JSON;
       }*/
            //set the access token in the header
            //params.put("Authorization", String.format("Bearer %s", preferences.getString("accessToken", null)));
            val headers: HashMap<String, String>
                @Throws(AuthFailureError::class)
                get() {
                    val preferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
                    val params: HashMap<String, String>
                    if (headerParams == null) {
                        params = HashMap<String, String>()
                    } else {
                        params = headerParams
                        params.put("timespan", preferences.getString("timespan", null))
                    }
                    return params
                }
            protected//set the access token in the header
            //params.put("Authorization", String.format("Bearer %s", preferences.getString("accessToken", null)));
            val params: HashMap<String, String>
                get() {
                    val params: HashMap<String, String>
                    if (requestParams == null) {
                        params = HashMap<String, String>()
                    } else {
                        params = requestParams
                    }
                    return params
                }

            protected override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                return super.parseNetworkResponse(response)
            }
        }
        // Adding String request to request queue
        stringRequest.setRetryPolicy(
            DefaultRetryPolicy(
                40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )
        addToRequestQueue(stringRequest, requestName)
    }

    private fun getRequestQueue(): RequestQueue? {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context)
        }
        return requestQueue
    }

    fun <T> addToRequestQueue(req: Request<T>, tag: String) {
        // set the default tag if tag is empty
        req.setTag(if (TextUtils.isEmpty(tag)) "testing" else tag)
        req.setRetryPolicy(DefaultRetryPolicy(90 * 1000, 0, 1.0f))
        getRequestQueue()?.add(req)
    }

    fun cancelPendingRequests(tag: Any) {
        if (requestQueue != null) {
            requestQueue!!.cancelAll(tag)
        }
    }

    interface OnRequestCompletedListener {
        fun onRequestCompleted(
            requestName: String, status: Boolean,
            response: String?, errorMessage: String?
        )
    }
}