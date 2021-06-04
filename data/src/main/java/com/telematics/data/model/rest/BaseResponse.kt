package com.telematics.data.model.rest

import com.google.gson.annotations.SerializedName

open class BaseResponse  {

    @SerializedName("Code") var code: Int = 0
    @SerializedName("Message") var message: String? = null
}