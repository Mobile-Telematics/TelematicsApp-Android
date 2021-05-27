package com.telematics.zenroad

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.telematics.domain_.listener.AuthenticationListener
import com.telematics.features.account.ui.AccountViewModel
import com.telematics.zenroad.databinding.ActivityMainBinding
import com.telematics.zenroad.ui.bottom_menu.ViewPagerFragmentStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AuthenticationListener {

    @Inject
    lateinit var authenticationRepo: AccountViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomMenu()

        authenticationRepo.setListener(this, null)
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

    override fun onLogout() {

        Log.d("Authentication", "Main activity logout")

        startActivity(Intent(this, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}