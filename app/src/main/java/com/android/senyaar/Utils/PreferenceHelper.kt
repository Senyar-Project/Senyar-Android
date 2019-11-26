package com.android.senyaar.Utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper(context: Context) {
    val PREFS_FILENAME = "MyPref"
    val SIGN_IN_TOKEN = "sign_in_token"
    val ROLE = "role"
    val LOGIN = "islogin"
    val USERNAME_PASSENGER = "user_name_passenger"
    val USERIMAGE_PASSENGER = "user_image_passenger"
    val USERNAME_DRIVER = "user_name_driver"
    val USERIMAGE_DRIVER = "user_image_driver"

    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0);
    var clearValues
        get() = { }
        set(value) =
            prefs.edit().clear().apply()

    var sign_in_token
        get() = prefs.getString(SIGN_IN_TOKEN, "")
        set(value) =
            prefs.edit().putString(SIGN_IN_TOKEN, value).apply()

    var isLogin
        get() = prefs.getString(LOGIN, "")
        set(value) =
            prefs.edit().putString(LOGIN, value).apply()

    var userImage_passenger
        get() = prefs.getString(USERIMAGE_PASSENGER, "")
        set(value) =
            prefs.edit().putString(USERIMAGE_PASSENGER, value).apply()

    var userName_passenger
        get() = prefs.getString(USERNAME_PASSENGER, "")
        set(value) =
            prefs.edit().putString(USERNAME_PASSENGER, value).apply()

    var userImage_driver
        get() = prefs.getString(USERIMAGE_DRIVER, "")
        set(value) =
            prefs.edit().putString(USERIMAGE_DRIVER, value).apply()

    var userName_driver
        get() = prefs.getString(USERNAME_DRIVER, "")
        set(value) =
            prefs.edit().putString(USERNAME_DRIVER, value).apply()

    var role
        get() = prefs.getStringSet(ROLE, null)
        set(value) =
            prefs.edit().putStringSet(ROLE, value).apply()


}
