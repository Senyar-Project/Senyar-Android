package com.android.senyaar.NetworkCalls;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by ATTech_Android_1 on 05/12/2018.
 */

public class VolleyClassCentralized {
    private Context context;
    private RequestQueue requestQueue;
    private OnRequestCompletedListener mRequestCompletedListener;

    public VolleyClassCentralized(Context context) {
        this.context = context;
    }

    /**
     * Used to call web service and get response as JSON using post method.
     *
     * @param context  - context of the activity.
     * @param callback - The callback reference.
     */
    public VolleyClassCentralized(Context context, OnRequestCompletedListener callback) {
        mRequestCompletedListener = callback;
        this.context = context;
    }

    /**
     * Request String response from the Web API.
     *
     * @param requestName   the String refers the request name
     * @param webserviceUrl the String refers the web service URL.
     * @param requestParams the list of parameters in byte array.
     * @param webMethod     the integer indicates the web method.
     * @param getCache      the boolean indicates whether cache can enable/disable
     */
    public void requestString(final String requestName,
                              final String webserviceUrl,
                              final HashMap<String, String> requestParams, final HashMap<String, String> headerParams,
                              final int webMethod,
                              final boolean getCache) {
        StringRequest stringRequest = new StringRequest(webMethod,
                webserviceUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                mRequestCompletedListener.onRequestCompleted(
                        requestName, true, response, null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("testing", "Request Name" + requestName);
                String errorResponse = "";
                boolean check = true;
                //Log.d("testing","Code"+error.networkResponse.statusCode+"DATA: "+error.networkResponse.data);
                if (error.networkResponse != null) {
                    //if (error.networkResponse.statusCode == 400 || error.networkResponse.statusCode == 406 || error.networkResponse.statusCode == 401 || error.networkResponse.statusCode == 500){
                    try {
                        String body = new String(error.networkResponse.data, "UTF-8");
                        JSONObject obj = new JSONObject(body);
                        if (obj.has("message")) {
                            if (obj.getString("message").isEmpty()) {
                                errorResponse = "Server error.Please try again.";
                            } else {
                                errorResponse = obj.getString("message");
                            }
                        } else if (obj.has("data")) {
                            if (obj.getString("data") != null) {
                                errorResponse = obj.getString("data");
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (error instanceof TimeoutError) {
                    errorResponse = "Connection Timed out.Please try again.";
                } else if (error instanceof NoConnectionError) {
                    if (error.getCause().getLocalizedMessage().equalsIgnoreCase("Chain Validation failed")) {
                        errorResponse = "SSL Handshake Exception-Chain Validation failed";
                    } else {
                        errorResponse = "No internet connection. Please try again.";
                    }
                } else if (error instanceof ParseError) {
                    errorResponse = "Parse error occurred. Please try again.";
                } else if (error instanceof NetworkError) {
                    errorResponse = "No network. Please try again.";
                } else if (error instanceof AuthFailureError) {
                    errorResponse = "Invalid email or password.";
                } else if (error instanceof ServerError) {
                    errorResponse = "Server error. Please try again.";
                } else {
                    errorResponse = error.toString();
                }
                if (check) {
                    mRequestCompletedListener.onRequestCompleted(
                            requestName, false, null,
                            errorResponse);
                }
            }
        }) {

            /* @Override
             public String getBodyContentType() {
                 return Configuration.APPLICATION_JSON;
             }*/

            @Override
            public HashMap<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params;
                String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                if (headerParams == null) {
                    params = new HashMap<>();
                } else {
                    params = headerParams;
                    //params.put("Authorization", String.format("Bearer %s", preferences.getString("accessToken", null)));
                }
                Log.d("testing", "request Name " + requestName + "API header: " + params);
                return params;
            }

            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> params;
                if (requestParams == null) {
                    params = new HashMap<>();
                } else {
                    params = requestParams;
                }
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };
        // Adding String request to request queue
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(stringRequest, requestName);
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? "testing" : tag);
        req.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 0, 1.0f));
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }

    /**
     * To get the ImageLoader class instance to load the network image in Image
     * view.
     *
     * @return ImageLoader instance.
     */


    /**
     * A callback interface indicates the status about the completion of HTTP
     * request.
     */
    public interface OnRequestCompletedListener {
        /**
         * Called when the String request has been completed.
         *
         * @param requestName  the String refers the request name
         * @param status       the status of the request either success or failure
         * @param response     the String response returns from the Webservice. It may be
         *                     null if request failed.
         * @param errorMessage the String refers the error message when request failed to
         *                     get the response
         */
        void onRequestCompleted(String requestName, boolean status,
                                String response, String errorMessage);
    }
}


