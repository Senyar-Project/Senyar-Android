package com.android.senyaar.Views

import com.android.senyaar.Adapters.ScheduledTripsAdapter
import com.android.senyaar.Model.generalResponse
import com.android.senyaar.Model.generalResponseArray
import com.android.senyaar.Model.scheduledTripModel

interface ScheduledTripView : BaseView {
    fun showAlltripsInList(adapter: ScheduledTripsAdapter)
    fun appClickListener(model: scheduledTripModel)
    fun getDriverList(response: generalResponseArray)
}