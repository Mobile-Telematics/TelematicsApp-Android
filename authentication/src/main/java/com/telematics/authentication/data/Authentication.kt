package com.telematics.authentication.data

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata
import com.telematics.authentication.exception.AuthException
import com.telematics.authentication.extention.await
import com.telematics.authentication.mapper.Mapper
import com.telematics.authentication.model.UserDatabase
import com.telematics.data.BuildConfig
import com.telematics.domain.model.RegistrationApiData
import com.telematics.domain.model.SessionData
import com.telematics.domain.model.authentication.*
import com.telematics.domain.model.company_id.InstanceName
import com.telematics.domain.repository.AuthenticationRepo
import com.telematics.domain.repository.SessionRepo
import com.telematics.domain.repository.UserRepo
import com.telematics.domain.repository.UserServicesRepo
import com.telematicssdk.auth.TelematicsAuth
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit


class Authentication constructor(
    private val authRepo: UserServicesRepo,
    private val sessionRepo: SessionRepo,
    private val userRepo: UserRepo,
    private val context: Context
) : AuthenticationRepo {

    private val TAG = "Authentication"

    private val INSTANCE_ID = BuildConfig.INSTANCE_ID
    private val INSTANCE_KEY = BuildConfig.INSTANCE_KEY

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = Firebase.database.reference
    private val firebaseStorage = FirebaseStorage.getInstance()
    private val firebaseStorageProfilePicRef = firebaseStorage.reference.child("profile_images")
    private val firebaseStorageProfilePicFormat = ".png"

    override suspend fun getCurrentUserID(): String? {
        val userId = firebaseAuth.currentUser?.uid
        Log.d(TAG, "getCurrentUserID: $userId")
        return userId
    }

    override suspend fun registrationCreateAPI(): RegistrationApiData {

        val createResult =
            TelematicsAuth.createDeviceToken(INSTANCE_ID, INSTANCE_KEY).await()
        //val data = authRepo.registration()
        return RegistrationApiData(
            createResult.deviceToken,
            createResult.accessToken,
            createResult.refreshToken,
            null
        )
    }

    override suspend fun loginAPI(deviceToken: String): SessionData {

        val loginResult = TelematicsAuth.login(INSTANCE_ID, INSTANCE_KEY, deviceToken).await()
        val data = SessionData(loginResult.accessToken, loginResult.refreshToken, null)
        //val data = authRepo.loginWithDeviceToken(deviceToken)
        sessionRepo.saveSession(data)
        return data
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

    override suspend fun getUserInFirebaseDatabase(userId: String): User {

        val userDatabase = firebaseDatabase.child("users").child(userId).await<UserDatabase>()
        val user = Mapper.userDatabaseToUser(userDatabase)
        Log.d(TAG, "getDeviceTokenInFirebaseDatabase: $user")
        userRepo.saveUser(user)
        return user
    }

    override suspend fun updateUserInFirebaseDatabase(user: IUser) {

        val newUser = user as User

        Log.d(TAG, "updateUser user:${newUser}")

        //update user in API user.telematicssdk.com
        authRepo.updateUser(newUser)

        val userDatabase = Mapper.userToUserDatabase(newUser)
        Log.d(TAG, "updateUser userDatabase:${userDatabase}")
        user.userId?.let { userId ->
            firebaseDatabase.child("users").child(userId).setValue(userDatabase).await()
        }
    }

    override suspend fun uploadProfilePicture(filePath: String?): String? {

        val userId = firebaseAuth.currentUser?.uid

        Log.d(TAG, "uploadProfilePicture: filePath: $filePath")
        Log.d(TAG, "uploadProfilePicture: userId: $userId")

        userId ?: return null
        filePath ?: return null

        saveProfilePicture(filePath)

        val imagesRef =
            firebaseStorageProfilePicRef.child("$userId$firebaseStorageProfilePicFormat")

        val metadata = storageMetadata {
            contentType = "application/octet-stream"
        }
        val fileUri = Uri.fromFile(File(filePath))
        imagesRef.putFile(fileUri, metadata).await()
        val uri = imagesRef.downloadUrl.await()

        Log.d(TAG, "uploadProfilePicture: image url $uri")

        //update user picture in API user.telematicssdk.com
        authRepo.updateUserPicture(filePath)

        return uri.toString()
    }

    override suspend fun downloadProfilePicture(): Bitmap? {

        val userId = firebaseAuth.currentUser?.uid
        userId ?: return null

        val imagesRef =
            firebaseStorageProfilePicRef.child("$userId$firebaseStorageProfilePicFormat")
        val tempFile = File(context.filesDir, "$userId")
        tempFile.createNewFile()
        imagesRef.getFile(tempFile).await()
        return BitmapFactory.decodeFile(tempFile.path)
    }

    override suspend fun getProfilePictureFromCache(): Bitmap? {

        val userId = firebaseAuth.currentUser?.uid
        userId ?: return null
        val tempFile = File(context.filesDir, "$userId")
        return BitmapFactory.decodeFile(tempFile.path)
    }

    private fun saveProfilePicture(filePath: String?) {

        val userId = firebaseAuth.currentUser?.uid
        userId ?: return

        val file = File(context.filesDir, "$userId")
        file.mkdir()
        val fOut = FileOutputStream(file)
        val bmp = BitmapFactory.decodeFile(filePath)
        bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut)
        fOut.flush()
        fOut.close()
    }

    private fun deleteProfilePicture() {

        val userId = firebaseAuth.currentUser?.uid
        userId ?: return

        val file = File(context.filesDir, "$userId")
        file.deleteOnExit()
    }

    override suspend fun logout(): Boolean {

        firebaseAuth.signOut()
        sessionRepo.clearSession()
        authRepo.logout()
        userRepo.clear()
        deleteProfilePicture()
        return true
    }

    override suspend fun loginWithDeviceToken(deviceToken: String): SessionData {
        val data = authRepo.loginWithDeviceToken(deviceToken)
        sessionRepo.saveSession(data)
        return data
    }

    override suspend fun changeCompanyId(companyId: String): InstanceName {
        return authRepo.changeCompanyId(companyId)
    }
}