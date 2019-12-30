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
    val status: String?,
    val driver_start_lat: String?,
    val driver_start_lon: String?,
    val passenger_start_lat: String?,
    val passenger_start_lon: String?,
    val passenger_end_lat: String?,
    val passenger_end_lon: String?,
    val passenger_pickup_location: String?


) : Serializable