package com.telematics.data.model.rest

import com.google.gson.annotations.SerializedName

data class ApiResponse<out Result>(

    @SerializedName("Result")
    val result: Result?,
    @SerializedName("Status")
    val status: Int,
    @SerializedName("Title")
    val msg: String? = null,
    @SerializedName("Errors")
    val details: List<ErrorData>? = null
) : BaseResponse()