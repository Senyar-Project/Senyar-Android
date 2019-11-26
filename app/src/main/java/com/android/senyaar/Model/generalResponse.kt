package com.android.senyaar.Model

import com.google.gson.annotations.SerializedName

class generalResponse {
    @SerializedName("code")
    var code: String? = null
    @SerializedName("message")
    var message: String? = null
    @SerializedName("data")
    var data: DataResponse? = null
}