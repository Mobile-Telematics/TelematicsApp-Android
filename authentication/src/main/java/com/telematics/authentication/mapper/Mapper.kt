package com.telematics.authentication.mapper

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.telematics.authentication.exception.AuthErrorCode
import com.telematics.authentication.exception.AuthException
import com.telematics.authentication.model.UserDatabase
import com.telematics.data.api.errors.ApiError
import com.telematics.data.extentions.*
import com.telematics.domain.model.authentication.User

class Mapper {
    companion object {
        private fun getErrorCode(exception: Exception?): AuthErrorCode {

            var error = ""

            if (exception is FirebaseException) {
                Log.d("getErrorCode", "getErrorCode: FirebaseException ${exception.message}")
                error = exception.message ?: ""
            }

            if (exception is FirebaseAuthException) {
                Log.d("getErrorCode", "getErrorCode: FirebaseAuthException ${exception.errorCode}")
                error = exception.errorCode
            }

            if (exception is FirebaseNetworkException) {
                Log.d("getErrorCode", "getErrorCode: FirebaseNetworkException")
                error = "NETWORK_EXCEPTION"
            }

            if (exception is AuthException) {
                Log.d("getErrorCode", "getErrorCode: 3 ${exception.errorCode}")
                return exception.errorCode
            }

            return when (error) {
                "ERROR_USER_NOT_FOUND" -> AuthErrorCode.USER_NOT_EXIST
                "ERROR_WRONG_PASSWORD" -> AuthErrorCode.INVALID_PASSWORD
                "ERROR_INVALID_VERIFICATION_CODE" -> AuthErrorCode.INVALID_VERIFICATION_CODE
                "ERROR_EMAIL_ALREADY_IN_USE" -> AuthErrorCode.EMAIL_ALREADY_IN_USE
                "NETWORK_EXCEPTION" -> AuthErrorCode.NETWORK_EXCEPTION
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

            val birthdayInString =
                try {
                    userDatabase.birthday
                        ?.iso8601InSecondsToLong()
                        ?.timeMillsToDisplayableString(DateFormat.DayMonthFullYear())
                } catch (e: Exception) {
                    Log.d("Mapper", "userDatabaseToUser: ${e.printStackTrace()}")
                    ""
                }

            return User().apply {
                email = userDatabase.email
                deviceToken = userDatabase.deviceToken
                userId = userDatabase.userId
                firstName = userDatabase.firstName
                lastName = userDatabase.lastName
                phone = userDatabase.phone
                birthday = birthdayInString
                address = userDatabase.address
                clientId = userDatabase.clientId
                profilePictureUrl = userDatabase.profilePictureLink
                gender = userDatabase.gender
                maritalStatus = userDatabase.maritalStatus
                childrenCount = userDatabase.childrenCount
            }
        }

        fun userToUserDatabase(user: User): UserDatabase {

            val birthdayInStr =
                user.birthday?.stringDateToTimeInMillis(DateFormat.DayMonthFullYear())
                    ?.timeMillsToIso8601InSeconds()

            return UserDatabase().apply {
                email = user.email
                firstName = user.firstName
                lastName = user.lastName
                phone = user.phone
                birthday = birthdayInStr ?: user.birthday
                address = user.address
                clientId = user.clientId
                userId = user.userId
                profilePictureLink = user.profilePictureUrl
                gender = user.gender
                maritalStatus = user.maritalStatus
                childrenCount = user.childrenCount
                //default
                deviceToken = user.deviceToken
            }
        }
    }
}