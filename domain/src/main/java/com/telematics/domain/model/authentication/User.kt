package com.telematics.domain.model.authentication

import java.io.Serializable

abstract class IUser(
    val id: String,
    val token: String
) {
    fun isEmpty(): Boolean {
        return id.isBlank()
    }
}

data class User(
    var email: String? = null,
    var password: String? = null,
    var deviceToken: String? = null,
    var userId: String? = null,

    var firstName: String? = null,
    var lastName: String? = null,
    var phone: String? = null,
    var birthday: String? = null,
    var address: String? = null,
    var clientId: String? = null,
    var profilePictureUrl: String? = null,

    var gender: String? = null,
    var maritalStatus: String? = null,
    var childrenCount: Int? = null
) : IUser(id = userId ?: "", token = deviceToken ?: ""), Serializable {

    fun isCompleted(): Boolean {
        return true
    }

    fun getNewUpdatedUser(newUser: User): User {

        return User().apply {
            this.deviceToken = this@User.deviceToken
            this.userId = this@User.userId

            this.email = newUser.email ?: this.email
            this.firstName = newUser.firstName ?: this.firstName ?: ""
            this.lastName = newUser.lastName ?: this.lastName ?: ""
            this.phone = newUser.phone ?: this.phone ?: ""
            this.birthday = newUser.birthday ?: this.birthday ?: ""
            this.address = newUser.address ?: this.address ?: ""
            this.clientId = newUser.clientId ?: this.clientId ?: ""
            this.profilePictureUrl = newUser.profilePictureUrl ?: this.profilePictureUrl ?: ""
            this.gender = newUser.gender ?: this.gender ?: ""
            this.maritalStatus = newUser.maritalStatus ?: this.maritalStatus ?: ""
            this.childrenCount = newUser.childrenCount ?: this.childrenCount ?: 0
        }
    }
}

data class FirebaseUser(
    var userId: String? = null,
) : IUser(id = userId ?: "", token = "")