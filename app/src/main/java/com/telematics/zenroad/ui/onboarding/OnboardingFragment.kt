package com.telematics.zenroad.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.telematics.content.utils.BaseFragment
import com.telematics.zenroad.R
import com.telematics.zenroad.databinding.FragmentOnboardingBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnboardingFragment : BaseFragment() {

    @Inject
    lateinit var viewModel: OnboardingViewModel

    private lateinit var adapter: OnboardingPageAdapter
    private lateinit var binding: FragmentOnboardingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initViewPager()
    }

    private fun initViewPager() {

        adapter = OnboardingPageAdapter().apply {
            val data = initData()
            setData(data)
            setOnClick(object : OnboardingPageAdapter.Listener {
                override fun onLastPageShowing() {

                }
            })
        }

        binding.onboardingViewPager.adapter = adapter
        binding.onboardingViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                onPageSelect(position)
            }
        })
        binding.onboardingIndicator.setViewPager(binding.onboardingViewPager)
    }

    private fun initData(): List<OnboardingPageAdapter.Data> {

        return mutableListOf(
            OnboardingPageAdapter.Data(
                R.string.onboarding_screen_1_title,
                R.string.onboarding_screen_1_text,
                R.drawable.bg_onboard_first
            ),
            OnboardingPageAdapter.Data(
                R.string.onboarding_screen_2_title,
                R.string.onboarding_screen_2_text,
                R.drawable.bg_onboard_second
            ),
            OnboardingPageAdapter.Data(
                R.string.onboarding_screen_3_title,
                R.string.onboarding_screen_3_text,
                R.drawable.bg_onboard_third
            ),
            OnboardingPageAdapter.Data(
                R.string.onboarding_screen_4_title,
                R.string.onboarding_screen_4_text,
                R.drawable.bg_onboard_fourth
            )
        )
    }

    private fun onPageSelect(position: Int) {

        val isLastPage = position >= adapter.itemCount - 1
        val text = if (isLastPage) R.string.lets_go else R.string.next
        binding.onboardingBtn.setText(text)
        binding.onboardingBtn.setOnClickListener {
            if (isLastPage)
                nextScreen()
            else
                showNextPage()
        }
    }

    private fun showNextPage() {

        var nextPosition = binding.onboardingViewPager.currentItem
        if (nextPosition < adapter.itemCount - 1) nextPosition++
        binding.onboardingViewPager.setCurrentItem(nextPosition, true)
    }

    private fun nextScreen() {

        viewModel.onboardingScreenComplete()
        findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
    }
}