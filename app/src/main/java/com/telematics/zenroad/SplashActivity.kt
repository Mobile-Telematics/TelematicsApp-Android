package com.telematics.zenroad

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.telematics.data_.repository.SessionRepo
import com.telematics.zenroad.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    @Inject
    lateinit var sessionRepository: SessionRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(IO).launch {
            navigate()
        }
    }

    private suspend fun navigate() {
        val intent =
            if (sessionRepository.isLoggedIn())
                Intent(this@SplashActivity, MainActivity::class.java)
            else
                Intent(this@SplashActivity, LoginActivity::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        withContext(Main) {
            startIntent(intent)
        }
    }

    private fun startIntent(intent: Intent) {
        startActivity(intent)
    }
}