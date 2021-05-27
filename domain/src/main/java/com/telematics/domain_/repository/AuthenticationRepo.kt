package com.telematics.domain_.repository

import com.telematics.domain_.listener.AuthenticationListener
import com.telematics.domain_.listener.UserUpdatedListener
import com.telematics.domain_.model.authentication.User

interface AuthenticationRepo {

    fun signInEmailPassword(email: String, password: String)
    fun registrationUser(email: String, password: String)
    fun setListener(listener: AuthenticationListener)
    fun setListener(listener: UserUpdatedListener)
    fun logout()
    fun isLoggedIn(): Boolean
    fun updateUser(user: User)

    fun tryLogin()
    suspend fun getUser(): User
}