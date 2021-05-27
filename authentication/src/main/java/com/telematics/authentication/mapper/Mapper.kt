package com.telematics.authentication.mapper

import com.google.firebase.auth.FirebaseAuthException
import com.telematics.authentication.model.UserDatabase
import com.telematics.domain_.error.ErrorCode
import com.telematics.domain_.model.authentication.User

class Mapper {
    companion object {
        fun getErrorCode(exception: Exception): ErrorCode {
            return if (exception is FirebaseAuthException)
                when (exception.errorCode) {
                    "ERROR_USER_NOT_FOUND" -> ErrorCode.USER_NOT_EXIST
                    "ERROR_WRONG_PASSWORD" -> ErrorCode.INVALID_PASSWORD
                    else -> ErrorCode.NONE
                }
            else ErrorCode.NONE
        }

        fun userDatabaseToUser(userDatabase: UserDatabase): User {

            return User().apply {
                email = userDatabase.email
                deviceToken = userDatabase.deviceToken
                userId = userDatabase.userId
                firstName = userDatabase.firstName
                lastName = userDatabase.lastName
                phone = userDatabase.phone
                birthday = userDatabase.birthday
                address = userDatabase.address
                clientId = userDatabase.clientId
            }
        }
    }
}