package com.telematics.domain.repository

import android.app.Activity
import com.telematics.domain.model.RegistrationApiData
import com.telematics.domain.model.SessionData
import com.telematics.domain.model.authentication.IUser
import com.telematics.domain.model.authentication.PhoneAuthCallback
import com.telematics.domain.model.authentication.PhoneAuthCred

interface AuthenticationRepo {

    suspend fun getCurrentUserID(): String?

    /** api */
    suspend fun registrationCreateAPI(): RegistrationApiData
    suspend fun loginAPI(deviceToken: String): SessionData

    /** sign in FirebaseAuth*/
    suspend fun signInWithEmailAndPasswordFirebase(email: String, password: String): IUser?
    suspend fun signInWithPhoneFirebase(
        phone: String,
        activity: Activity,
        callback: PhoneAuthCallback
    )

    suspend fun signInWithPhoneAuthCredential(credential: PhoneAuthCred<*>): IUser?
    suspend fun sendVerificationCode(
        phone: String,
        code: String,
        verificationId: String
    ): PhoneAuthCred<*>

    /** create in FirebaseAuth*/
    suspend fun createUserWithEmailAndPasswordInFirebase(email: String, password: String): IUser?

    /** create in FirebaseDatabase*/
    suspend fun createUserInFirebaseDatabase(user: IUser)

    /** get FirebaseDatabase*/
    suspend fun getUserInFirebaseDatabase(userId: String): IUser?

    /** update user in FirebaseDatabase */
    suspend fun updateUserInFirebaseDatabase(user: IUser)

    /** logout*/
    suspend fun logout(): Boolean
}