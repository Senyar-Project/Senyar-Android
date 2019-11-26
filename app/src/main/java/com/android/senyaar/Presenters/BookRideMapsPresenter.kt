package com.android.senyaar.Presenters

import com.android.senyaar.Model.BookRideMapsModel
import com.android.senyaar.Model.generalResponse
import com.android.senyaar.Model.generalResponseArray
import com.android.senyaar.Utils.PreferenceHelper
import com.android.senyaar.Views.BookRideMapsView
import com.android.senyaar.Views.DashboardPassengerView
import org.json.JSONObject

class BookRideMapsPresenter(private var view: BookRideMapsView, var model: BookRideMapsModel) :
    BookRideMapsModel.OnFinishedListener {
    init {
        view.initView()
    }

    override fun onResultSuccess(response: String, tag: String) {
        view.hideProgress()
        if (tag.equals("get")) {
            view.getDriver(response)
        } else {
            view.bookRide(response)
        }
    }

    override fun onResultFail(strError: String, tag: String) {
        view.hideProgress()
        view.showServerError(strError, tag)
    }


    fun getDrivers(
        get_driver_json: JSONObject,
        prefs: PreferenceHelper
    ) {
        view.showProgress()
        model.getDrivers(this, get_driver_json, prefs)
    }

    fun bookRide(
        book_ride_json: JSONObject,
        prefs: PreferenceHelper
    ) {
        view.showProgress()
        model.bookRide(this, book_ride_json, prefs)
    }
}