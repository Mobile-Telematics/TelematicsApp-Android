package com.telematics.data.model.company_id

import com.google.gson.annotations.SerializedName

data class InstanceNameBody(
        @SerializedName("InstanceName")
        val instanceName: String?
)