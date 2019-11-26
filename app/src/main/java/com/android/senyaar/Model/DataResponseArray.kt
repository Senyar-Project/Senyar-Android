package com.android.senyaar.Model

import com.google.gson.annotations.SerializedName

class DataResponseArray {
    @SerializedName("id")
    var id: String? = null
    @SerializedName("shcedule_id")
    var shcedule_id: String? = null
    @SerializedName("first_name")
    var first_name: String? = null
    @SerializedName("last_name")
    var last_name: String? = null
    @SerializedName("distance")
    var distance: String? = null

    //// For history
    @SerializedName("rider_id")
    var rider_id: String? = null
    @SerializedName("captain_id")
    var captain_id: String? = null
    @SerializedName("schedule_id")
    var schedule_id: String? = null
    @SerializedName("ride_id")
    var ride_id: String? = null
    @SerializedName("rider_pickup_long")
    var rider_pickup_long: String? = null
    @SerializedName("rider_pickup_lat")
    var rider_pickup_lat: String? = null
    @SerializedName("rider_pickup_location_name")
    var rider_pickup_location_name: String? = null
    @SerializedName("rider_dropoff_long")
    var rider_dropoff_long: String? = null
    @SerializedName("rider_dropoff_lat")
    var rider_dropoff_lat: String? = null
    @SerializedName("roder_dropoff_location_name")
    var roder_dropoff_location_name: String? = null
    @SerializedName("captain_start_long")
    var captain_start_long: String? = null
    @SerializedName("captain_start_lat")
    var captain_start_lat: String? = null
    @SerializedName("captain_start_location_name")
    var captain_start_location_name: String? = null
    @SerializedName("captain_end_long")
    var captain_end_long: String? = null
    @SerializedName("captain_end_lat")
    var captain_end_lat: String? = null
    @SerializedName("captain_en_location_name")
    var captain_en_location_name: String? = null
    @SerializedName("schedule_status")
    var schedule_status: String? = null
    @SerializedName("rider_first_name")
    var rider_first_name: String? = null
    @SerializedName("rider_last_name")
    var rider_last_name: String? = null
    @SerializedName("schedule_date")
    var schedule_date: String? = null
    @SerializedName("schedule_time")
    var schedule_time: String? = null


    //For Riders in Book Activity
    @SerializedName("start_long")
    var start_long: String? = null
    @SerializedName("start_lat")
    var start_lat: String? = null
    @SerializedName("end_long")
    var end_long: String? = null
    @SerializedName("profile_picture")
    var profile_picture: String? = null
    @SerializedName("rating")
    var rating: String? = null
}