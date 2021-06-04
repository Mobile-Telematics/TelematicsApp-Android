package com.telematics.zenroad.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.telematics.zenroad.R
import com.telematics.zenroad.databinding.MainFragmentBinding
import com.telematics.zenroad.ui.bottom_menu.ViewPagerFragmentStateAdapter

class MainFragment : Fragment() {

    lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBottomMenu()
    }

    private fun initBottomMenu() {

        val viewPager = binding.mainViewPager
        val bottomNavigationView = binding.mainBottomNav

        viewPager.apply {
            isUserInputEnabled = false
            //offscreenPageLimit = 4
        }
        viewPager.adapter = ViewPagerFragmentStateAdapter(requireActivity())

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_dashboard -> {
                    viewPager.setCurrentItem(0, false)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_profile -> {
                    viewPager.setCurrentItem(1, false)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    viewPager.setCurrentItem(0, false)
                    return@setOnNavigationItemSelectedListener true
                }
            }
        }
    }
}