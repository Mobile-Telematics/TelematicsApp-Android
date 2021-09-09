package com.telematics.zenroad.ui.settings.company_id

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.telematics.content.utils.BaseFragment
import com.telematics.zenroad.R
import com.telematics.zenroad.databinding.CompanyIdFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CompanyIDFragment : BaseFragment() {

    lateinit var binding: CompanyIdFragmentBinding

    @Inject
    lateinit var companyIdViewModel: CompanyIdViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CompanyIdFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    private fun setListeners() {

        binding.profileCompanyIdBack.setOnClickListener {
            onBackPressed()
        }

        binding.profileCompanyIdSend.setOnClickListener {
            sendCompanyId(binding.profileCompanyIdInput.text.toString())
        }

        binding.profileCompanyIdInput.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                sendCompanyId(binding.profileCompanyIdInput.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun sendCompanyId(companyId: String) {

        hideKeyboard()

        companyIdViewModel.send(companyId).observe(viewLifecycleOwner) { result ->
            result.onSuccess { instanceName ->
                if (instanceName.isSuccess) {
                    val name = instanceName.name ?: ""
                    showSuccessMsg(name)
                } else
                    showError(R.string.company_screen_invalid_code)
            }
            result.onFailure {
                showError(R.string.company_screen_invalid_code)
            }
        }
    }

    private fun showError(res: Int) {
        showError(getString(res))
    }

    private fun showError(msg: String) {

        val oldText = binding.profileCompanyIdText.text

        binding.profileCompanyIdText.text = msg
        binding.profileCompanyIdText.setTextColor(Color.RED)

        Handler(Looper.getMainLooper()).postDelayed({
            try {
                binding.profileCompanyIdText.text = oldText
                binding.profileCompanyIdText.setTextColor(Color.BLACK)
            } catch (e: Exception) {
            }
        }, 3000L)

    }

    private fun showSuccessMsg(msg: String) {

        val text = getString(R.string.dashboard_settings_company_id_success, " $msg")
        showMessage(text)
    }
}