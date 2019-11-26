package com.android.senyaar.NetworkCalls;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Created by ATTech_Android_1 on 18/10/2018.
 */

public class JsonRequest {
    // private static final String TAG = Logger.makeLogTag(VolleyRequestHelper.class);
    private Context context;
    private RequestQueue requestQueue;
    private OnRequestCompletedListener mRequestCompletedListener;

    public JsonRequest(Context context) {
        this.context = context;
    }


    /**
     * Used to call web service and get response as JSON using post method.
     *
     * @param context  - context of the activity.
     * @param callback - The callback reference.
     */
    public JsonRequest(Context context, OnRequestCompletedListener callback) {
        mRequestCompletedListener = callback;
        this.context = context;
    }

    /**
     * Request String response from the Web API.
     *
     * @param requestName   the String refers the request name
     * @param webserviceUrl the String refers the web service URL.
     * @param webMethod     the integer indicates the web method.
     * @param getCache      the boolean indicates whether cache can enable/disable
     */
    public void requestString(final String requestName,
                              final String webserviceUrl, final JSONObject json,
                              final HashMap<String, String> headerParams,
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
                String errorResponse = "";
//                Log.d("testing","Code"+error.networkResponse.statusCode+"DATA: "+error.networkResponse.data);
                if (error.networkResponse != null) {
                    // if (error.networkResponse.statusCode == 400 || error.networkResponse.statusCode == 406 || error.networkResponse.statusCode == 401 || error.networkResponse.statusCode == 500){
                    try {
                        String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
              /*      }
                    else
                    {
                        try {
                            String body = new String(error.networkResponse.data, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        errorResponse = "Server error.Please try again.";
                    }*/

                } else if (error instanceof TimeoutError) {
                    errorResponse = "Connection Timed out.Please try again.";
                } else if (error instanceof NoConnectionError) {
                    errorResponse = "No internet connection. Please try again.";
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
                mRequestCompletedListener.onRequestCompleted(
                        requestName, false, null,
                        errorResponse);
            }
        }) {
            @Override
            public HashMap<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params;
                SharedPreferences preferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                if (headerParams == null) {
                    params = new HashMap<String, String>();
                } else {
                    params = headerParams;
                    //set the access token in the header
                    //params.put("Authorization", String.format("Bearer %s", preferences.getString("accessToken", null)));
                }

                return params;
            }


            @Override
            public byte[] getBody() throws AuthFailureError {
                //add the json object to the body of the API request
                return json.toString().getBytes();
            }

        };
        // Adding String request to request queue
        addToRequestQueue(stringRequest, requestName);
    }

    public RequestQueue getRequestQueue() {
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

