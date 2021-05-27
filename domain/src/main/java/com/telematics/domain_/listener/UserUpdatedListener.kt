package com.telematics.domain_.listener

import com.telematics.domain_.model.authentication.User

interface UserUpdatedListener {
    fun onUserUpdated(user: User) {}
    fun onUserUpdateFailure() {}
}