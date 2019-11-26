package com.android.senyaar.Utils

import android.graphics.drawable.Drawable
import android.graphics.PorterDuff
import android.graphics.Bitmap
import android.R.layout
import android.view.View.MeasureSpec
import android.R
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.support.v4.content.ContextCompat.getSystemService
import android.view.LayoutInflater
import android.support.annotation.DrawableRes
import android.util.Log
import android.view.View
import com.android.senyaar.Activities.LoginOptionActivity
import com.android.senyaar.Activities.SignUpDriverActivity
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.regex.Matcher
import java.util.regex.Pattern


object CommonMethods {
    fun emailVerification(email: String?): Boolean {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordStrong(pass: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[-@#!£$%^&*()_+~`{}\\[\\]:\"'?,.\\\\/])(?=.*[A-Z])(?=.*[a-z])(?=\\S+$).{8,}$"
        //String PASSWORD_PATTERN="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(pass)
        return matcher.matches()
    }

    fun driverRedirects(prefs: PreferenceHelper, activityName: String, context: Context): Boolean {
        if (prefs.isLogin.equals("driver")) {
            if (prefs.sign_in_token != null && prefs.role != null) {
                if (prefs.role.contains("ROLE_DRIVER")) {
                    Log.d("testing", "Role" + prefs.role)
                    return true
                }
            }
        } else {
            val intent = Intent(context, LoginOptionActivity::class.java)
            intent.putExtra("activityName", activityName)
            intent.putExtra("role", "ROLE_DRIVER")
            context.startActivity(intent)
            return false
        }
        return false
    }

    fun passengerRedirect(prefs: PreferenceHelper, activityName: String, context: Context): Boolean {
        if (prefs.isLogin.equals("passenger")) {
            if (prefs.sign_in_token != null && prefs.role != null) {
                if (prefs.role.contains("ROLE_CUSTOMER")) {
                    Log.d("testing", "Role" + prefs.role)
                    return true
                }
            }
        } else {
            val intent = Intent(context, LoginOptionActivity::class.java)
            intent.putExtra("activityName", activityName)
            intent.putExtra("role", "ROLE_CUSTOMER")
            context.startActivity(intent)
            return false
        }
        return false
    }


}