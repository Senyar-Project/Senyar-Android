package com.android.senyaar.Views

import com.android.senyaar.Model.generalResponseArray

interface BookRideMapsView : BaseView {
    fun showSnackbar(message: String)
    fun getDriver(response: String)
    fun bookRide(response: String)

}
