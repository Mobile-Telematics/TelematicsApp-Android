package com.telematics.authentication.data

import android.app.Activity
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.telematics.authentication.exception.AuthException
import com.telematics.authentication.extention.await
import com.telematics.authentication.mapper.Mapper
import com.telematics.authentication.model.UserDatabase
import com.telematics.domain.model.RegistrationApiData
import com.telematics.domain.model.SessionData
import com.telematics.domain.model.authentication.*
import com.telematics.domain.repository.AuthenticationRepo
import com.telematics.domain.repository.SessionRepo
import com.telematics.domain.repository.UserRepo
import com.telematics.domain.repository.UserServicesRepo
import java.util.concurrent.TimeUnit


//use like login use case
class Authentication constructor(
    private val authRepo: UserServicesRepo,
    private val sessionRepo: SessionRepo,
    private val userRepo: UserRepo
) : AuthenticationRepo {

    private val TAG = "Authentication"

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = Firebase.database.reference

    override suspend fun getCurrentUserID(): String? {
        val userId = firebaseAuth.currentUser?.uid
        //val userId = userRepo.getUserId()
        Log.d(TAG, "getCurrentUserID: $userId")
        return userId
    }

    override suspend fun registrationCreateAPI(): RegistrationApiData {
        return authRepo.registration()
    }

    override suspend fun loginAPI(deviceToken: String): SessionData {
        return authRepo.loginWithDeviceToken(deviceToken)
    }

    override suspend fun signInWithEmailAndPasswordFirebase(
        email: String,
        password: String
    ): IUser {

        val firebaseUser = firebaseAuth.signInWithEmailAndPassword(email, password).await().user
        Log.d(TAG, "signInWithEmailAndPasswordFirebase: ${firebaseUser?.uid}")
        return FirebaseUser(firebaseUser?.uid)
    }

    override suspend fun signInWithPhoneFirebase(
        phone: String,
        activity: Activity,
        callback: PhoneAuthCallback
    ) {
        Log.d(TAG, "signInWithPhoneFirebase: phone: $phone")

        val phoneAuthOptions = PhoneAuthOptions.newBuilder()
            .setPhoneNumber(phone)
            .setActivity(activity)
            .setTimeout(30, TimeUnit.SECONDS)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    Log.d(TAG, "signInWithPhoneFirebase: onCodeAutoRetrievalTimeOut")
                    //callback.onTimeout()
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    Log.d(TAG, "signInWithPhoneFirebase: onCodeSent verificationId: $p0")
                    callback.onCodeSend(p0)
                }

                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    Log.d(TAG, "signInWithPhoneFirebase: onVerificationCompleted")
                    callback.onCompleted(PhoneAuthCred(p0, phone))
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Log.d(TAG, "onVerificationFailed: ${p0.message}")
                    val exception = Mapper.getErrorCodeByException(p0)
                    callback.onFailure(AuthException(exception))
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)
    }

    override suspend fun signInWithPhoneAuthCredential(credential: PhoneAuthCred<*>): IUser {
        Log.d(TAG, "signInWithPhoneAuthCredential")

        val c = credential.credential as PhoneAuthCredential
        val firebaseUser = firebaseAuth.signInWithCredential(c).await().user
        Log.d(TAG, "signInWithPhoneAuthCredential userid: ${firebaseUser?.uid}")
        return FirebaseUser(firebaseUser?.uid)
    }

    override suspend fun sendVerificationCode(
        phone: String,
        code: String,
        verificationId: String
    ): PhoneAuthCred<*> {
        Log.d(TAG, "sendVerificationCode code: $code verificationId: $verificationId")

        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        return PhoneAuthCred(credential, phone)
    }

    override suspend fun createUserWithEmailAndPasswordInFirebase(
        email: String,
        password: String
    ): IUser {

        val firebaseUser = firebaseAuth.createUserWithEmailAndPassword(email, password).await().user
        Log.d(TAG, "createUserWithEmailAndPasswordInFirebase: ${firebaseUser?.uid}")
        return FirebaseUser(firebaseUser?.uid)
    }

    override suspend fun createUserInFirebaseDatabase(user: IUser) {

        user as User

        Log.d(TAG, "createUserInFirebaseDatabase $user")

        val userDatabase = UserDatabase()
        userDatabase.deviceToken = user.deviceToken
        userDatabase.email = user.email
        userDatabase.phone = user.phone
        Log.d(TAG, "createUserInFirebaseDatabase userDatabase $userDatabase")
        firebaseDatabase.child("users").child(user.userId!!).setValue(userDatabase).await()
    }

    override suspend fun getUserInFirebaseDatabase(userId: String): User? {

        val user = firebaseDatabase.child("users").child(userId).await<User>()
        Log.d(TAG, "getDeviceTokenInFirebaseDatabase: $user")
        userRepo.saveUser(user)
        return user
    }

    override suspend fun updateUserInFirebaseDatabase(user: IUser) {

        val newUser = user as User

        Log.d(TAG, "updateUser user:${newUser}")

        val userDatabase = UserDatabase().apply {
            this.email = newUser.email ?: user.email
            this.firstName = newUser.firstName ?: user.firstName
            this.lastName = newUser.lastName ?: user.lastName
            this.phone = newUser.phone ?: user.phone
            this.birthday = newUser.birthday ?: user.birthday
            this.address = newUser.address ?: user.address
            this.clientId = newUser.clientId ?: user.clientId
            this.userId = newUser.userId ?: user.userId

            //default
            this.deviceToken = user.deviceToken
            //this.image = newUser.image ?: user.image
        }
        Log.d(TAG, "updateUser userDatabase:${userDatabase}")
        user.userId?.let { userId ->
            firebaseDatabase.child("users").child(userId).setValue(userDatabase).await()
        }
    }

    override suspend fun logout(): Boolean {

        firebaseAuth.signOut()
        sessionRepo.clearSession()
        authRepo.logout()
        userRepo.clear()
        return true
    }
}