package com.telematics.zenroad.ui.settings

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.telematics.content.utils.BaseFragment
import com.telematics.content.utils.TryOpenLink
import com.telematics.data.BuildConfig
import com.telematics.zenroad.R
import com.telematics.zenroad.databinding.SettingsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : BaseFragment() {

    companion object {
        const val NAV_TO_KEY = "nav_to"
        const val NAV_TO_ACCOUNT = "account"
    }

    lateinit var binding: SettingsFragmentBinding

    @Inject
    lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    private fun setListeners() {

        binding.settingsToolbar.setNavigationOnClickListener {
            openMainFragment()
        }

        binding.settingsProfile.setOnClickListener {
            openAccountFragment()
        }

        binding.settingsOBD.setOnClickListener {
            openOBDFragment()
        }

        binding.settingsMeasures.setOnClickListener {
            openMeasures()
        }

        binding.settingsCompanyID.setOnClickListener {
            openCompanyIdFragment()
        }

        binding.settingsLogout.setOnClickListener {
            logout()
        }

        binding.settingsPrivacy.setOnClickListener {
            openPrivacy()
        }

        binding.settingsTerms.setOnClickListener {
            openTerms()
        }
    }

    private fun logout() {

        fun goLogout() {
            settingsViewModel.logout().observe(viewLifecycleOwner) { result ->
                result.onSuccess {
                    openSplashFragment()
                }
                result.onFailure { }
            }
        }

        AlertDialog.Builder(context)
            .setMessage(R.string.nav_logout_question)
            .setCancelable(true)
            .setPositiveButton(R.string.nav_logout_yes) { d, _ ->
                goLogout()
                d.dismiss()
            }
            .setNegativeButton(R.string.nav_logout_no) { d, _ ->
                d.dismiss()
            }
            .show()
    }

    private fun openPrivacy() {

        val link = BuildConfig.PRIVACY_POLICY
        TryOpenLink(requireContext()).open(link)
    }

    private fun openTerms() {

        val link = BuildConfig.TERMS_OF_USE
        TryOpenLink(requireContext()).open(link)
    }

    private fun openSplashFragment() {

        findNavController().navigate(R.id.action_settingsFragment_to_splashFragment)
    }

    private fun openMainFragment() {

        onBackPressed()
    }

    private fun openAccountFragment() {

        val bundle = bundleOf(NAV_TO_KEY to NAV_TO_ACCOUNT)
        findNavController().navigate(R.id.action_settingsFragment_to_mainFragment, bundle)
    }

    private fun openOBDFragment() {

        findNavController().navigate(R.id.action_settingsFragment_to_obdFragment)
    }

    private fun openCompanyIdFragment() {

        findNavController().navigate(R.id.action_settingsFragment_to_companyIdFragment)
    }

    private fun openMeasures() {

        findNavController().navigate(R.id.action_settingsFragment_to_measuresFragment)
    }
}