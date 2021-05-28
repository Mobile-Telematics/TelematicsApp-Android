package com.telematics.authentication.data

import android.app.Activity
import android.util.Log
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
import com.telematics.data_.extentions.setLiveData
import com.telematics.data_.utils.Resource
import com.telematics.domain_.error.ErrorCode
import com.telematics.domain_.listener.AuthenticationListener
import com.telematics.domain_.listener.UserUpdatedListener
import com.telematics.domain_.model.SessionData
import com.telematics.domain_.model.authentication.User
import com.telematics.domain_.repository.AuthenticationRepo
import com.telematics.domain_.repository.SessionRepo
import com.telematics.domain_.repository.UserServicesRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


//use like login usecase
class Authentication constructor(
    private val authRepo: UserServicesRepo,
    private val sessionRepo: SessionRepo
) : AuthenticationRepo {

    private val TAG = "Authentication"

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = Firebase.database.reference

    private var authenticationListener: AuthenticationListener? = null
    private var userUpdatedListener: UserUpdatedListener? = null
    private var user = User()

    private var phoneVerificationId = ""

    /** PUBLIC */
    override fun signInEmailPassword(email: String, password: String) {

        user.email = email
        user.password = password

        Log.d(TAG, "signInEmailPassword start e:$email p:$password")
        tryAuthWithEmailPassword(email, password)
    }

    override fun signInPhone(phone: String, activity: Activity?) {
        Log.d(TAG, "signInPhone  phone: $phone")
        user.phone = phone
        tryAuthWithPhone(phone, activity)
    }

    override fun checkPhoneVerificationCode(code: String) {

        checkPhoneCode(code)
    }

    override fun registrationUser(email: String, password: String?) {

        firebaseAuth.currentUser?.let {
            Log.d(TAG, "registrationUser currentUser exists")
            user.userId = it.uid
            registrationInApi(email)
            return
        }

        Log.d(TAG, "registrationUser start e:$email p:$password")

        firebaseAuth.createUserWithEmailAndPassword(email, password.orEmpty())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "registrationUser isSuccessful")
                    it.result?.user?.uid?.let { userId ->
                        user.userId = userId
                        registrationInApi(email)
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

    override fun logout() {

        Log.d(TAG, "logout")

        CoroutineScope(IO).launch {
            sessionRepo.clearSession()
            authRepo.logout()
        }
        firebaseAuth.signOut()
        this.authenticationListener?.onLogout()
    }

    override fun tryLogin() {
        Log.d(TAG, "tryLogin")
        firebaseAuth.currentUser?.uid?.let { userId ->
            Log.d(TAG, "tryLogin user: $userId")
            user.userId = userId
            getUserDeviceToken(userId)
        } ?: run {
            Log.d(TAG, "tryLogin user: null")
            error(ErrorCode.USER_NOT_EXIST)
        }
    }

    override fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override fun setListener(listener: AuthenticationListener) {
        this.authenticationListener = listener
    }

    override fun setListener(listener: UserUpdatedListener) {
        this.userUpdatedListener = listener
    }

    override fun updateUser(newUser: User) {

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
                update(newUser)
            }
            .addOnFailureListener {
                updateFail()
            }
    }

    override suspend fun getUser(): User {
        user
        return user
    }

    /** AUTHENTICATE EMAIL */
    private fun tryAuthWithEmailPassword(email: String, password: String) {

        Log.d(TAG, "loginWithEmailPassword start e:$email p:$password")

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, "loginWithEmailPassword isSuccessful")
                it.result?.user?.uid?.let { userId ->
                    user.userId = userId
                    getUserDeviceToken(userId)
                } ?: run {
                    //error
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
    private fun tryAuthWithPhone(phone: String, activity: Activity?) {

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
                    authenticationListener?.onLoginNeedPhoneCode()
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
                error(ErrorCode.NONE)
            }
        }
    }

    /** REGISTRATION */
    private fun registrationInApi(email: String) {

        Log.d(TAG, "registrationInApi start")

        val registrationMutableLiveData = MutableLiveData<Resource<SessionData>>()
        registrationMutableLiveData.observeForever {
            when (it) {
                is Resource.Success -> {
                    Log.d(TAG, "registrationInApi Success")
                    user.deviceToken = it.data!!.deviceToken
                    createUserInDatabase(user)
                }
                is Resource.Failure -> {
                    Log.d(TAG, "registrationInApi Failure")
                    error(ErrorCode.NONE)
                }
                is Resource.Loading -> {
                }
            }
        }
        flow {
            //fixme: use email
            val tempEmail = "${System.currentTimeMillis()}@temp.dev"
            emit(authRepo.registrationWithEmail(tempEmail))
        }.setLiveData(registrationMutableLiveData)
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
        val loginMutableLiveData = MutableLiveData<Resource<SessionData>>()
        loginMutableLiveData.observeForever {
            when (it) {
                is Resource.Success -> {
                    Log.d(TAG, "loginInApi Success")
                    user.accessToken = it.data!!.accessToken
                    success(it.data!!)
                }
                is Resource.Failure -> {
                    Log.d(TAG, "loginInApi Failure")
                }
                is Resource.Loading -> {
                }
            }
        }

        flow {
            emit(authRepo.loginWithDeviceToken(deviceToken))
        }
            .setLiveData(loginMutableLiveData)
            .launchIn(MainScope())

    }

    /** CALLBACKS */
    private fun update(user: User) {

        this.userUpdatedListener?.onUserUpdated(user)
    }

    private fun updateFail() {
        this.userUpdatedListener?.onUserUpdateFailure()
    }

    private fun success(sessionData: SessionData) {
        CoroutineScope(IO).launch {
            sessionRepo.saveSession(sessionData)
        }
        this.authenticationListener?.onLoginSuccess(sessionData)
    }

    private fun error(errorCode: ErrorCode) {
        this.authenticationListener?.onLoginFailure(errorCode)
    }
}