package com.android.senyaar.Model

import com.google.gson.annotations.SerializedName

class DataResponse {

    //For Sign In
    @SerializedName("token")
    var token: String? = null
    @SerializedName("roles")
    //var roles=MutableSet<String>?=null
    var roles: Array<String>? = null


    //For Get Profile
    @SerializedName("first_name")
    var first_name: String? = null
    @SerializedName("last_name")
    var last_name: String? = null
    @SerializedName("username")
    var username: String? = null
    @SerializedName("licence_number")
    var licence_number: String? = null
    @SerializedName("mobile_number")
    var mobile_number: String? = null
    @SerializedName("vechicle_number")
    var vechicle_number: String? = null
    @SerializedName("profile_picture")
    var profile_picture: String? = null

    //For Fare estimate
    @SerializedName("estimated_fare")
    var estimated_fare: String? = null

}