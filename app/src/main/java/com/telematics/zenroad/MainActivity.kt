package com.telematics.zenroad

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.telematics.zenroad.databinding.MainActivityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onBackPressed() {
        Log.d("MAIN_A", "onBackPressed: GO")
        Log.d("MAIN_A", "onBackPressed: ${supportFragmentManager.backStackEntryCount}")
        Log.d("MAIN_A", "onBackPressed: ${supportFragmentManager.fragments.size}")

        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }
}