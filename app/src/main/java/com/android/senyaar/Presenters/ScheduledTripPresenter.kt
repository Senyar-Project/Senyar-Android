package com.android.senyaar.Presenters

import com.android.senyaar.Adapters.ScheduledTripsAdapter
import com.android.senyaar.Model.ScheduledTripDataFeeder
import com.android.senyaar.Model.generalResponse
import com.android.senyaar.Model.generalResponseArray
import com.android.senyaar.Model.scheduledTripModel
import com.android.senyaar.Utils.PreferenceHelper
import com.android.senyaar.Views.ScheduledTripView

class ScheduledTripPresenter(view: ScheduledTripView, model: ScheduledTripDataFeeder) :
    ScheduledTripDataFeeder.OnFinishedListener {
    override fun onResultSuccess(response: generalResponseArray) {
        view.hideProgress()
        view.getDriverList(response)    }

    override fun onResultFail(strError: String, tag: String) {
        view.hideProgress()
        view.showServerError(strError, tag)    }

    private var view: ScheduledTripView = view
    var adapter: ScheduledTripsAdapter? = null
    private var model: ScheduledTripDataFeeder = model

    init {
        view.initView()
    }

    fun displayAllData(list:List<scheduledTripModel>) {
        adapter = ScheduledTripsAdapter(list, clickListener = {
            view.appClickListener(it)
        })
        view.showAlltripsInList(adapter!!)
    }

    fun getScheduledRides(prefs: PreferenceHelper,url:String) {
        view.showProgress()
        model.getScheduledRides(this, prefs,url)
    }
}