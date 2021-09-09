package com.telematics.features.account.use_case

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import com.telematics.domain.model.LoginType
import com.telematics.domain.model.SessionData
import com.telematics.domain.model.authentication.PhoneAuthCallback
import com.telematics.domain.model.authentication.PhoneAuthCred
import com.telematics.domain.model.authentication.User
import com.telematics.domain.model.company_id.InstanceName
import com.telematics.domain.repository.AuthenticationRepo
import com.telematics.domain.repository.UserRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authenticationRepo: AuthenticationRepo,
    private val userRepo: UserRepo
) {

    private val TAG = "LoginUseCase"

    private var isPictureUploading = false

    fun isSessionAvailable(): Flow<Boolean> {
        Log.d(TAG, "isSessionAvailable")

        //todo: preprod #test login with device token
        //return testLogin("")

        return flow {
            var result = false
            authenticationRepo.getCurrentUserID()?.let { currentUid ->
                authenticationRepo.getUserInFirebaseDatabase(currentUid)
                    ?.let { iUser ->
                        val user = iUser as User
                        user.userId = currentUid
                        val deviceToken = user.deviceToken
                        if (!deviceToken.isNullOrEmpty()) {
                            val sessionData = authenticationRepo.loginAPI(deviceToken)
                            if (!sessionData.isEmpty()) {
                                saveUser(user)
                            }
                            result = !sessionData.isEmpty()
                        }
                    }
            }

            if (!result)
                logout()
            emit(result)
        }
    }

    fun authorize(
        email: String,
        password: String
    ): Flow<Boolean> {
        Log.d(TAG, "authorize email")

        return flow {
            val result = authorizeByEmail(email, password)
            if (!result)
                logout()
            emit(result)
        }
    }

    fun authorize(
        phone: String,
        activity: Activity,
        callback: PhoneAuthCallback
    ): Flow<Boolean> {
        Log.d(TAG, "authorize phone")

        return flow {
            authenticationRepo.signInWithPhoneFirebase(phone, activity, callback)
            emit(true)
        }
    }

    fun authorize(credential: PhoneAuthCred<*>): Flow<Boolean> {
        Log.d(TAG, "authorize credential")

        return flow {
            val result = authoriseWithPhoneCredentials(credential.phone, credential)
            if (!result)
                logout()
            emit(result)
        }
    }

    fun registration(login: String, password: String, loginType: LoginType): Flow<Boolean> {
        Log.d(TAG, "registration")

        return flow {
            var result = false
            authenticationRepo.createUserWithEmailAndPasswordInFirebase(login, password)
                ?.let { firebaseUser ->
                    val user = when (loginType) {
                        LoginType.EMAIL -> User(email = login, userId = firebaseUser.id)
                        LoginType.PHONE -> User(phone = login, userId = firebaseUser.id)
                    }
                    val sessionData = registerUser(user)
                    if (!sessionData.isEmpty()) {
                        result = true
                    }
                }
            if (!result)
                logout()
            emit(result)
        }
    }

    fun sendVerifyCode(phone: String, code: String, verificationId: String): Flow<Boolean> {
        Log.d(TAG, "setVerifyCode: phone $phone code $code verificationId $verificationId")

        return flow {

            val cred = authenticationRepo.sendVerificationCode(phone, code, verificationId)
            val result = authoriseWithPhoneCredentials(phone, cred)

            if (!result)
                logout()
            emit(result)
        }
    }

    fun logout(): Flow<Boolean> {

        Log.d(TAG, "logout")
        return flow {
            emit(authenticationRepo.logout())
        }
    }

    private suspend fun authorizeByEmail(email: String, password: String): Boolean {
        var result = false

        authenticationRepo.signInWithEmailAndPasswordFirebase(email, password)?.let { iUser ->
            val userDatabase =
                authenticationRepo.getUserInFirebaseDatabase(iUser.id) as User?
            val deviceToken = userDatabase?.deviceToken
            val sessionData = if (deviceToken.isNullOrEmpty()) {
                val user = User(email = email, password = password, userId = iUser.id)
                registerUser(user)
            } else {
                authenticationRepo.loginAPI(deviceToken)
            }
            if (!sessionData.isEmpty()) {
                saveUser(userDatabase)
                result = true
            }
        }

        return result
    }

    private suspend fun registerUser(user: User): SessionData {
        Log.d(TAG, "registrationUser")

        val registrationApiData = authenticationRepo.registrationCreateAPI()
        val deviceToken = registrationApiData.deviceToken
        user.deviceToken = deviceToken
        authenticationRepo.createUserInFirebaseDatabase(user)
        val sessionData = authenticationRepo.loginAPI(deviceToken)
        if (!sessionData.isEmpty()) {
            saveUser(user)
            userRepo.saveUser(user)
        }
        return sessionData
    }

    private suspend fun authoriseWithPhoneCredentials(
        phone: String,
        credential: PhoneAuthCred<*>
    ): Boolean {
        var result = false
        authenticationRepo.signInWithPhoneAuthCredential(credential)?.let { iUser ->
            val userDatabase =
                (authenticationRepo.getUserInFirebaseDatabase(iUser.id) as User?)
            val deviceToken = userDatabase?.deviceToken
            userDatabase?.userId = iUser.id
            val sessionData = if (deviceToken.isNullOrEmpty()) {
                val user = User(phone = phone, userId = iUser.id)
                registerUser(user)
            } else {
                authenticationRepo.loginAPI(deviceToken)
            }

            if (!sessionData.isEmpty()) {
                saveUser(userDatabase)
                result = true
            }
        }

        return result
    }

    private suspend fun saveUser(user: User?) {
        Log.d(TAG, "saveUser: user:$user")
        userRepo.saveUserId(user?.userId)
        userRepo.saveDeviceToken(user?.deviceToken)
    }

    fun getUser(): Flow<User> {
        return flow {
            val user = userRepo.getUser()
            emit(user)
        }
    }

    fun updateUser(user: User): Flow<Unit> {
        return flow {
            val oldUser = userRepo.getUser()
            val newUser = oldUser.getNewUpdatedUser(user)
            val userId = userRepo.getUserId()
            newUser.userId = userId
            userRepo.saveUser(newUser)
            authenticationRepo.updateUserInFirebaseDatabase(newUser)
            emit(Unit)
        }
    }

    fun uploadProfilePicture(filePath: String?): Flow<Unit> {

        isPictureUploading = true
        return flow {
            val profilePictureUrl = authenticationRepo.uploadProfilePicture(filePath)
            val newUser = userRepo.getUser()
            newUser.profilePictureUrl = profilePictureUrl
            newUser.userId = userRepo.getUserId()
            userRepo.saveUser(newUser)
            authenticationRepo.updateUserInFirebaseDatabase(newUser)
            Log.d(TAG, "uploadProfilePicture: profilePictureUrl $profilePictureUrl")
            isPictureUploading = false
            emit(Unit)
        }
    }

    fun getProfilePicture(): Flow<Bitmap?> {
        return flow {
            val pictureUrl = userRepo.getUser().profilePictureUrl
            Log.d(TAG, "getProfilePicture: pictureUrl $pictureUrl")
            val bitmapCache = authenticationRepo.getProfilePictureFromCache()
            emit(bitmapCache)
            if (isPictureUploading) {
                return@flow
            }
            val bitmap = authenticationRepo.downloadProfilePicture()
            emit(bitmap)
        }
    }

    fun changeCompanyId(companyId: String): Flow<InstanceName> {

        return flow {
            val data = authenticationRepo.changeCompanyId(companyId)
            emit(data)
        }
    }

    //todo: preprod #test login with device token
    fun testLogin(deviceToken: String): Flow<Boolean> {

        return flow {
            val sessionData =
                authenticationRepo.loginWithDeviceToken(deviceToken)
            val user = User()
            user.firstName = "Test user name"
            user.deviceToken = deviceToken
            saveUser(user)
            emit(!sessionData.isEmpty())
        }
    }
}