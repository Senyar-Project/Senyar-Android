package com.android.senyaar.Presenters

import com.android.senyaar.Model.ProfileModel
import com.android.senyaar.Utils.PreferenceHelper
import com.android.senyaar.Views.ProfileView
import org.json.JSONObject

class ProfilePresenter(private var view: ProfileView, var model: ProfileModel) :
    ProfileModel.OnFinishedListener {
    init {
        view.initView()
    }

    override fun onResultSuccess(response: String, tag: String) {
        view.hideProgress()
        if (tag.equals("get")) {
            view.getProfile(response)
        } else {
            view.updateProfile(response)
        }
    }

    override fun onResultFail(strError: String, tag: String) {
        view.hideProgress()
        view.showServerError(strError, tag)
    }


    fun getProfile(
        prefs: PreferenceHelper
    ) {
        view.showProgress()
        model.getProfile(this, prefs)
    }

    fun updateProfile(
        update_profile_json: JSONObject,
        prefs: PreferenceHelper
    ) {
        view.showProgress()
        model.updateProfile(this, update_profile_json, prefs)
    }
}