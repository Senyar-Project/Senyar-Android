package com.android.senyaar.Model

import java.io.Serializable

data class scheduledTripModel(
    val journey: Int,
    val departure: String?,
    val arrival: String?,
    val date: String?,
    val time: String?,
    val rider_id: String?,
    val ride_id: String?,
    val captain_id: String?,
    val schedule_id: String?,
    val status: String?
):Serializable