package com.telematics.domain.model.authentication

data class User(
    var email: String? = null,
    var password: String? = null,
    var deviceToken: String? = null,
    var accessToken: String? = null,
    var userId: String? = null,

    var firstName: String? = null,
    var lastName: String? = null,
    var phone: String? = null,
    var birthday: String? = null,
    var address: String? = null,
    var clientId: String? = null
    //var image: String? = null,
) {

    fun isCompleted(): Boolean {
        return true
    }

    fun setNotNullUserField(newUser: User) {

        this.email = newUser.email ?: this.email
        this.firstName = newUser.firstName ?: this.firstName
        this.lastName = newUser.lastName ?: this.lastName
        this.phone = newUser.phone ?: this.phone
        this.birthday = newUser.birthday ?: this.birthday
        this.address = newUser.address ?: this.address
        this.clientId = newUser.clientId ?: this.clientId
    }
}