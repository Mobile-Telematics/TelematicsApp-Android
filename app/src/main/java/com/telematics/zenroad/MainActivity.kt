package com.telematics.zenroad

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.telematics.domain_.repository.SessionRepo
import com.telematics.zenroad.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import com.telematics.zenroad.ui.bottom_menu.ViewPagerFragmentStateAdapter
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var sessionRepository: SessionRepo
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomMenu()

        getSessionData()
    }

    private fun initBottomMenu() {

        val viewPager2 = binding.viewPager
        val bottomNavigationView = binding.bottomNav

        viewPager2.apply {
            isUserInputEnabled = false
            //offscreenPageLimit = 4
        }

        viewPager2.adapter = ViewPagerFragmentStateAdapter(this)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_dashboard -> {
                    viewPager2.setCurrentItem(0, false)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_profile -> {
                    viewPager2.setCurrentItem(1, false)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    viewPager2.setCurrentItem(0, false)
                    return@setOnNavigationItemSelectedListener true
                }
            }
        }
    }

    private fun getSessionData() {

        CoroutineScope(Dispatchers.IO).launch {
            val sessionData = sessionRepository.getSession()
            Log.d("MainActivity", "refreshToken: " + sessionData.refreshToken)
            Log.d("MainActivity", "accessToken: " + sessionData.accessToken)
            Log.d("MainActivity", "deviceToken: " + sessionData.deviceToken)
            Log.d("MainActivity", "expiresIn: " + sessionData.expiresIn.toString())
        }
    }
}