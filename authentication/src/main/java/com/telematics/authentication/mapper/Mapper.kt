package com.telematics.authentication.mapper

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthException
import com.telematics.authentication.exception.AuthErrorCode
import com.telematics.authentication.exception.AuthException
import com.telematics.authentication.model.UserDatabase
import com.telematics.data.api.errors.ApiError
import com.telematics.domain.model.authentication.User

class Mapper {
    companion object {
        private fun getErrorCode(exception: Exception?): AuthErrorCode {

            var error = ""

            if (exception is FirebaseException) {
                Log.d("getErrorCode", "getErrorCode: 1 ${exception.message}")
                error = exception.message ?: ""
            }

            if (exception is FirebaseAuthException) {
                Log.d("getErrorCode", "getErrorCode: 2 ${exception.errorCode}")
                error = exception.errorCode
            }

            if (exception is AuthException) {
                Log.d("getErrorCode", "getErrorCode: 3 ${exception.errorCode}")
                return exception.errorCode
            }

            return when (error) {
                "ERROR_USER_NOT_FOUND" -> AuthErrorCode.USER_NOT_EXIST
                "ERROR_WRONG_PASSWORD" -> AuthErrorCode.INVALID_PASSWORD
                "ERROR_INVALID_VERIFICATION_CODE" -> AuthErrorCode.INVALID_VERIFICATION_CODE
                /*api status*/
                "422" -> AuthErrorCode.INVALID_PASSWORD
                else -> AuthErrorCode.NONE
            }
        }

        fun getErrorCodeByException(exception: Exception?): AuthErrorCode {
            return getErrorCode(exception)
        }

        fun getErrorCodeByThrowable(throwable: Throwable?): AuthErrorCode {
            return if (throwable is ApiError) {
                getErrorCode(Exception(throwable.errorCode.toString()))
            } else
                getErrorCode(Exception(throwable))
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