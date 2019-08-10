package com.android.senyaar.Views

import com.android.senyaar.Utils.PreferenceHelper

interface BaseView {
    fun initView()
    fun showProgress()
    fun hideProgress()
    fun getPreferences(): PreferenceHelper
}