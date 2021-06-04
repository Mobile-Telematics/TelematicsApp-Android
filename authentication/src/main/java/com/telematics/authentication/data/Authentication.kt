package com.telematics.authentication.data

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.telematics.authentication.mapper.Mapper
import com.telematics.authentication.model.UserDatabase
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.domain.error.ErrorCode
import com.telematics.domain.model.LoginType
import com.telematics.domain.model.SessionData
import com.telematics.domain.model.authentication.User
import com.telematics.domain.repository.AuthenticationRepo
import com.telematics.domain.repository.SessionRepo
import com.telematics.domain.repository.UserServicesRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


//use like login use case
class Authentication constructor(
    private val authRepo: UserServicesRepo,
    private val sessionRepo: SessionRepo
) : AuthenticationRepo {

    private val TAG = "Authentication"

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = Firebase.database.reference

    private var user = User()
    private var phoneVerificationId = ""
    private var activityForPhoneAuth: Activity? = null

    /** LIVE DATA */
    private val loginLiveData = MutableLiveData<Result<SessionData>>()
    private val userLiveData = MutableLiveData<Result<User>>()
    private val phoneVerificationMutableLiveData = MutableLiveData<Result<Boolean>>()

    /** PUBLIC */
    override suspend fun signInEmailPassword(email: String, password: String) {

        Log.d(TAG, "signInEmailPassword start e:$email p:$password")
        user.email = email
        user.password = password
        registrationInApi(LoginType.EMAIL)
    }

    override suspend fun signInPhone(phone: String, activity: Activity?) {

        Log.d(TAG, "signInPhone  phone: $phone")
        user.phone = phone
        activityForPhoneAuth = activity
        registrationInApi(LoginType.PHONE)
    }

    override suspend fun login() {

        Log.d(TAG, "tryLogin")
        firebaseAuth.currentUser?.uid?.let { userId ->
            Log.d(TAG, "tryLogin user: $userId")
            user.userId = userId
            getUserDeviceToken(userId)
        } ?: run {
            Log.d(TAG, "tryLogin user: null")
            error(ErrorCode.EMPTY_SESSION)
        }
    }

    override fun needPhoneVerification(): LiveData<Result<Boolean>> {
        return phoneVerificationMutableLiveData
    }

    override suspend fun checkPhoneVerificationCode(code: String) {

        checkPhoneCode(code)
    }

    override suspend fun registrationUser(email: String, password: String?) {

        user.email = email
        user.password = password

        firebaseAuth.currentUser?.let {
            Log.d(TAG, "registrationUser currentUser exists")
            user.userId = it.uid
            registrationInApi(LoginType.EMAIL)
            return
        }

        Log.d(TAG, "registrationUser start e:$email p:$password")

        firebaseAuth.createUserWithEmailAndPassword(email, password.orEmpty())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "registrationUser isSuccessful")
                    it.result?.user?.uid?.let { userId ->
                        user.userId = userId
                        registrationInApi(LoginType.EMAIL)
                    } ?: run {
                        //error
                    }
                } else {
                    Log.d(TAG, "registrationUser isFailed")
                    error(ErrorCode.NONE)
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "registrationUser addOnFailureListener:${it.message}")
            }
    }

    override suspend fun logout() {

        Log.d(TAG, "logout")

        firebaseAuth.signOut()
        sessionRepo.clearSession()
        authRepo.logout()
        onLogout()
    }

    override fun getSessionData(): MutableLiveData<Result<SessionData>> {
        return loginLiveData
    }

    override suspend fun updateUser(newUser: User) {

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
        user.setNotNullUserField(newUser)
        Log.d(TAG, "updateUser userDatabase:${userDatabase}")
        firebaseDatabase.child("users").child(this.user.userId!!).setValue(userDatabase)
            .addOnCompleteListener {
                onUpdate(newUser)
            }
            .addOnFailureListener {
                onUpdateFail()
            }
    }

    override fun getUser(): MutableLiveData<Result<User>> {
        userLiveData.postValue(Result.success(user))
        return userLiveData
    }

    /** AUTHENTICATE EMAIL */
    private fun tryAuthWithEmailPassword(
        email: String = user.email!!,
        password: String = user.password!!
    ) {

        Log.d(TAG, "loginWithEmailPassword start e:$email p:$password")

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, "loginWithEmailPassword isSuccessful")
                it.result?.user?.uid?.let { userId ->
                    user.userId = userId
                    getUserDeviceToken(userId)
                } ?: run {
                    error(ErrorCode.NONE)
                }
            } else {
                Log.d(TAG, "loginWithEmailPassword isFailed")
            }
        }.addOnFailureListener {
            val errorCode = Mapper.getErrorCode(it)
            Log.d(TAG, "loginWithEmailPassword isFailed code: $errorCode MGS: ${it.message}")
            this.error(errorCode)
        }
    }

    /** AUTHENTICATE PHONE */
    private fun tryAuthWithPhone(
        phone: String = user.phone!!,
        activity: Activity? = activityForPhoneAuth
    ) {

        Log.d(TAG, "tryAuthWithPhone start p:$phone")

        activity ?: run {
            error(ErrorCode.NONE)
            return
        }

        firebaseAuth.setLanguageCode("en")

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(30L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    Log.d(TAG, "tryAuthWithPhone onVerificationCompleted")
                    signInWithPhoneAuthCredential(p0)
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    Log.d(TAG, "tryAuthWithPhone onCodeSent")
                    phoneVerificationId = p0
                    needVerificationCode()
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Log.d(TAG, "tryAuthWithPhone onVerificationFailed")
                    val errorCode = Mapper.getErrorCode(p0)
                    error(errorCode)
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    error(ErrorCode.LOGIN_TIMEOUT)
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun checkPhoneCode(code: String) {
        Log.d(TAG, "checkPhoneCode code: $code phoneVerificationId: $phoneVerificationId")
        if (code.isBlank()) {
            error(ErrorCode.EMPTY_VERIFICATION_CODE)
            return
        }
        val credential = PhoneAuthProvider.getCredential(phoneVerificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Log.d(TAG, "signInWithPhoneAuthCredential")

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            Log.d(TAG, "signInWithPhoneAuthCredential isSuccessful: ${task.isSuccessful}")
            if (task.isSuccessful) {
                val userId = task.result?.user?.uid!!
                user.userId = userId
                getUserDeviceToken(userId)
            } else {
                error(ErrorCode.INVALID_VERIFICATION_CODE)
            }
        }
    }

    /** REGISTRATION */
    private fun registrationInApi(loginType: LoginType) {

        Log.d(TAG, "registrationInApi start")

        val registrationMutableLiveData = MutableLiveData<Result<SessionData>>()
        registrationMutableLiveData.observeForever { result ->
            result.onSuccess {
                Log.d(TAG, "registrationInApi Success")
                user.deviceToken = it.deviceToken
                createUserInDatabase(user)
            }
            result.onFailure {
                Log.d(TAG, "registrationInApi Failure")
                when (loginType) {
                    LoginType.EMAIL -> tryAuthWithEmailPassword()
                    LoginType.PHONE -> tryAuthWithPhone()
                }
            }
        }
        flow {
            val data = when (loginType) {
                LoginType.EMAIL -> authRepo.registrationWithEmail(user.email!!)
                LoginType.PHONE -> authRepo.registrationWithPhone(user.phone!!)
            }
            emit(data)
        }.setLiveDataForResult(registrationMutableLiveData)
            .launchIn(MainScope())
    }

    private fun createUserInDatabase(user: User) {

        Log.d(TAG, "createUserInDatabase start user:${user}")

        val userDatabase = UserDatabase()
        userDatabase.deviceToken = user.deviceToken
        userDatabase.email = user.email
        firebaseDatabase.child("users").child(user.userId!!).setValue(userDatabase)
        loginInApi(userDatabase.deviceToken!!)
    }

    /** LOGIN */
    private fun getUserDeviceToken(userId: String) {

        Log.d(TAG, "getUserDeviceToken start userId:$userId")

        val url = firebaseDatabase.database.app.options.databaseUrl
        Log.d(TAG, "getUserDeviceToken start url: $url")

        firebaseDatabase.child("users").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userDatabase = snapshot.getValue(UserDatabase::class.java)
                    Log.d(TAG, "getUserDeviceToken user:${userDatabase}")
                    userDatabase?.deviceToken?.let { deviceToken ->
                        user = Mapper.userDatabaseToUser(userDatabase)
                        loginInApi(deviceToken)
                    } ?: run {
                        Log.d(TAG, "getUserDeviceToken user: null")
                        error(ErrorCode.USER_NOT_EXIST)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "getUserDeviceToken onCancelled:${error.message}")
                    error(ErrorCode.NONE)
                }
            })
    }

    private fun loginInApi(deviceToken: String) {

        Log.d(TAG, "loginInApi start deviceToken:${deviceToken}")

        user.deviceToken = deviceToken
        val loginMutableLiveData = MutableLiveData<Result<SessionData>>()
        loginMutableLiveData.observeForever { result ->
            result.onSuccess {
                Log.d(TAG, "loginInApi Success")
                user.accessToken = it.accessToken
                onSuccess(it)
            }
            result.onFailure {
                Log.d(TAG, "loginInApi Failure")
                error(ErrorCode.NONE)
            }
        }

        flow {
            emit(authRepo.loginWithDeviceToken(deviceToken))
        }
            .setLiveDataForResult(loginMutableLiveData)
            .launchIn(MainScope())
    }

    /** CALLBACKS */
    private fun needVerificationCode() {

        phoneVerificationMutableLiveData.postValue(Result.success(true))
    }

    private fun onUpdate(user: User) {

        userLiveData.postValue(Result.success(user))
    }

    private fun onUpdateFail() {

        //this.userUpdatedListener?.onUserUpdateFailure()
    }

    private fun onSuccess(sessionData: SessionData) {

        CoroutineScope(IO).launch {
            sessionRepo.saveSession(sessionData)
        }
        loginLiveData.postValue(Result.success(sessionData))
        //this.authenticationListener?.onLoginSuccess(sessionData)
    }

    private fun error(errorCode: ErrorCode) {

        Log.d(TAG, "error: error${errorCode.name}")

        sessionRepo.clearSession()
        loginLiveData.postValue(Result.failure(AuthException(errorCode)))
    }

    private fun onLogout() {
        loginLiveData.postValue(Result.failure(AuthException(ErrorCode.EMPTY_SESSION)))
    }

    class AuthException(val errorCode: ErrorCode) : Exception()
}