package com.telematics.zenroad.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.telematics.zenroad.R
import com.telematics.zenroad.databinding.SplashFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    @Inject
    lateinit var viewModel: SplashViewModel

    lateinit var binding: SplashFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SplashFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        login()
    }

    private fun login() {

        viewModel.isSessionAvailable().observe(viewLifecycleOwner) { result ->
            result.onSuccess { isSessionAvailable ->
                if (isSessionAvailable)
                    goToMainScreen()
                else
                    nextScreen()
            }
            result.onFailure {
                nextScreen()
            }
        }
    }

    private fun nextScreen() {

        viewModel.needOpenOnboarding().observe(viewLifecycleOwner) { result ->
            result.onSuccess { needOpenOnboarding ->
                if (needOpenOnboarding)
                    openOnboardingScreen()
                else
                    openLoginScreen()
            }
            result.onFailure {
                openOnboardingScreen()
            }
        }
    }

    private fun openOnboardingScreen() {

        findNavController().navigate(R.id.action_splash_to_onboardingFragment)
    }

    private fun openLoginScreen() {

        findNavController().navigate(R.id.action_splash_to_loginFragment)
    }

    private fun goToMainScreen() {

        findNavController().navigate(R.id.action_splash_to_mainFragment)
    }
}