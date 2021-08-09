package com.telematics.data.model.login

import com.google.gson.annotations.SerializedName

data class UserUpdateBody(
    @SerializedName("Phone")
    val phone: String? =null,
    @SerializedName("Email")
    val email: String? =null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("birthday")
    val birthday: String? = null,
    @SerializedName("childrenCount")
    val childrenCount: Int? = null,
    @SerializedName("firstName")
    val firstName: String? = null,
    @SerializedName("gender")
    val gender: String? = null,
    @SerializedName("lastName")
    val lastName: String? = null,
    @SerializedName("maritalStatus")
    val maritalStatus: String? = null,
    @SerializedName("ClientID")
    val clientId: String? = null,
    @SerializedName("imageUrl")
    val imageUrl: String? = null
)