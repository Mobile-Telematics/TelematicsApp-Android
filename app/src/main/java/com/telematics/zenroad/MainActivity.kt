package com.telematics.zenroad

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.telematics.authentication.data.Authentication
import com.telematics.domain.error.ErrorCode
import com.telematics.features.account.use_case.LoginUseCase
import com.telematics.zenroad.databinding.MainActivityBinding
import com.telematics.zenroad.extention.isEmpty
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var accountViewModel: LoginUseCase
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.AppTheme)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        login()
    }

    private fun login() {

        mainViewModel.login().observe(this) { result ->
            result?.onSuccess { sessionData ->
                if (sessionData.isEmpty())
                    goToLogin()
                else
                    goToDashboard()
            }
            result?.onFailure {
                val error = it as Authentication.AuthException
                when (error.errorCode) {
                    ErrorCode.INVALID_PASSWORD -> {
                        showLoginFailedMessage("Incorrect password")
                        return@onFailure
                    }
                    ErrorCode.USER_NOT_EXIST -> {
                        return@onFailure
                    }
                    ErrorCode.LOGIN_TIMEOUT -> {
                        showLoginFailedMessage("Login verification timeout")
                    }
                    ErrorCode.EMPTY_VERIFICATION_CODE -> {
                        showLoginFailedMessage("Incorrect verification code")
                        return@onFailure
                    }
                    ErrorCode.INVALID_VERIFICATION_CODE -> {
                        showLoginFailedMessage("Incorrect verification code")
                        return@onFailure
                    }
                    ErrorCode.EMPTY_SESSION -> {

                    }
                    ErrorCode.NONE -> {
                        //showLoginFailedMessage("Unknown error")
                        return@onFailure
                    }
                }

                goToLogin()
            }
        }
    }

    private fun goToLogin() {

        findNavController(R.id.nav_host).setGraph(R.navigation.nav_graph)
        findNavController(R.id.nav_host).navigate(R.id.action_navFragment_to_loginFragment)
    }

    private fun goToDashboard() {

        findNavController(R.id.nav_host).setGraph(R.navigation.nav_graph)
        findNavController(R.id.nav_host).navigate(R.id.action_navFragment_to_mainFragment)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        return
        Log.d("MAIN_A", "onBackPressed: GO")
        Log.d("MAIN_A", "onBackPressed: ${supportFragmentManager.backStackEntryCount}")

        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    private fun showLoginFailedMessage(message: String) {
        Log.d("LoginActivity", "message: $message")
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}