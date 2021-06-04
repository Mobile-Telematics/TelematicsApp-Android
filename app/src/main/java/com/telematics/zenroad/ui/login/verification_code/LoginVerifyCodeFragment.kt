package com.telematics.zenroad.ui.login.verification_code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.telematics.authentication.data.Authentication
import com.telematics.authentication.extention.observeOnce
import com.telematics.domain.error.ErrorCode
import com.telematics.features.account.use_case.LoginUseCase
import com.telematics.zenroad.R
import com.telematics.zenroad.databinding.ActivityVerifyCodeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

@AndroidEntryPoint
class LoginVerifyCodeFragment : Fragment() {

    companion object {
        const val RESULT_CODE_KEY = "CODE_KEY"
    }

    @Inject
    lateinit var loginUseCase: LoginUseCase

    private lateinit var binding: ActivityVerifyCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityVerifyCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val phone = arguments?.getString("phone") ?: ""
        binding.verifyMessage.text = requireContext().getString(
            R.string.verify_code_screen_message,
            phone
        )

        setListeners()
    }

    private fun setListeners() {

        binding.verifyAnotherEmail.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.verifySend.setOnClickListener {
            setResult(binding.verifyInputCode.text.toString())
        }

        binding.verifyInputCode.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setResult(binding.verifyInputCode.text.toString())
            }
            return@setOnEditorActionListener true
        }


    }

    private fun bindTitle(phone: String?) {

        binding.verifyMessage.text = getString(R.string.verify_code_screen_message, phone.orEmpty())
    }

    private fun setResult(code: String?) {

        if (!validFields()) {
            return
        }

        loginUseCase.getSessionData().observeOnce(viewLifecycleOwner) { result ->
            result.onSuccess {

            }
            result.onFailure {

                val error = (it as Authentication.AuthException).errorCode
                when(error){
                    ErrorCode.INVALID_VERIFICATION_CODE -> showErrorMessage("Invalid code")
                }
            }
        }

        loginUseCase.setVerifyCode(code)
            .launchIn(MainScope())
    }

    private fun validFields(): Boolean {

        var valid = true

        val code = binding.verifyInputCode.text.toString()
        if (code.isBlank()) {
            valid = false
            showErrorMessage("The code must be filled")
        }

        return valid
    }

    private fun showErrorMessage(msg: String) {

        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
    }
}