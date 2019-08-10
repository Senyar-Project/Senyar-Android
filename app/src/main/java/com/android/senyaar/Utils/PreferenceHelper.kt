package com.android.senyaar.Utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper(context: Context) {
    val PREFS_FILENAME = "MyPref"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0);
    var clearValues
        get() = { }
        set(value) =
            prefs.edit().clear().apply()
}