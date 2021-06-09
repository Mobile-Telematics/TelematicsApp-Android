package com.telematics.zenroad.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
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
                    goToDashboard()
                else
                    goToLogin()
            }
            result.onFailure {
                goToLogin()
            }
        }
    }

    private fun goToLogin() {

        requireActivity().findNavController(R.id.nav_host)
            .navigate(R.id.action_splash_to_loginFragment)
    }

    private fun goToDashboard() {

        requireActivity().findNavController(R.id.nav_host)
            .navigate(R.id.action_splash_to_mainFragment)
    }
}