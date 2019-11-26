package com.android.senyaar.Utils;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.android.senyaar.R;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


/**
 * Created by ATTech_Android_1 on 02/12/2017.
 */
public class MyApplication extends Application {
    //Declare a private  RequestQueue variable
    private RequestQueue requestQueue;
    private static MyApplication mInstance;
    ProgressDialog pDialog;

    public void onCreate() {
        super.onCreate();
        pDialog=progressdialog(this);
        MultiDex.install(this);
        mInstance=this;
        //registerActivityLifecycleCallbacks(this);
    }


    public static synchronized MyApplication getInstance() {
        return mInstance;
    }
    /**
     * Create a getRequestQueue() method to return the instance of
     * RequestQueue.This kind of implementation ensures that
     * the variable is instatiated only once and the same
     * instance is used throughout the application
     **/
    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        return requestQueue;
    }
    //function for the creation of the progress dialog box
    public ProgressDialog progressdialog(Context context) {
        pDialog = new ProgressDialog(context,R.style.CustomDialog);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("");
        pDialog.setCancelable(false);
        return pDialog;
    }
}
