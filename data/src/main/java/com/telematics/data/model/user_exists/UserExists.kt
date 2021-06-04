package com.telematics.data.model.user_exists

import com.google.gson.annotations.SerializedName

data class UserExists(
    @SerializedName("FirstName")
    val firstName: String?,
    @SerializedName("ImageUrl")
    val imageUrl: String?,
    @SerializedName("LastName")
    val lastName: String?,
    @SerializedName("Nickname")
    val nickname: String?,
    @SerializedName("UserExists")
    val userExists: Boolean
)