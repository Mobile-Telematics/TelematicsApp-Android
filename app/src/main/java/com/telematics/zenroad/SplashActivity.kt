package com.telematics.zenroad

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.telematics.domain_.error.ErrorCode
import com.telematics.domain_.listener.AuthenticationListener
import com.telematics.domain_.model.SessionData
import com.telematics.domain_.repository.SessionRepo
import com.telematics.features.account.databinding.AccountNavigationFragmentBinding
import com.telematics.features.account.ui.AccountViewModel
import com.telematics.zenroad.databinding.ActivityMainBinding
import com.telematics.zenroad.databinding.SplashActivityBinding
import com.telematics.zenroad.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity(), AuthenticationListener {
    @Inject
    lateinit var accountViewModel: AccountViewModel

    lateinit var binding: SplashActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SplashActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigate()
    }

    private fun navigate() {

        accountViewModel.setListener(this, null)
        accountViewModel.tryLogin()
    }

    override fun onLoginSuccess(sessionData: SessionData) {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startIntent(intent)
    }

    override fun onLoginFailure(errorCode: ErrorCode) {
        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        startIntent(intent)
    }

    private fun startIntent(intent: Intent) {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}