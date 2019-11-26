package com.android.senyaar.Model

import com.google.gson.annotations.SerializedName

class generalResponseArray {
    @SerializedName("code")
    var code: String? = null
    @SerializedName("message")
    var message: String? = null
    @SerializedName("data")
    var data:  Array<DataResponseArray>? = null
}