package com.telematics.zenroad.ui.login.verification_code

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.annotation.StringRes
import androidx.navigation.fragment.findNavController
import com.telematics.authentication.exception.AuthErrorCode
import com.telematics.authentication.exception.AuthException
import com.telematics.content.utils.BaseFragment
import com.telematics.domain.model.authentication.PhoneAuthCallback
import com.telematics.domain.model.authentication.PhoneAuthCred
import com.telematics.zenroad.R
import com.telematics.zenroad.databinding.ActivityVerifyCodeBinding
import com.telematics.zenroad.ui.login.LoginFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginVerifyCodeFragment : BaseFragment() {

    private val TAG = "LoginVerifyCodeFragment"

    @Inject
    lateinit var viewModel: LoginVerifyCodeViewModel

    private lateinit var binding: ActivityVerifyCodeBinding

    private lateinit var phone: String
    private var verificationId: String = ""

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

        setBackPressedCallback()

        phone = arguments?.getString(LoginFragment.BUNDLE_LOGIN_KEY) ?: ""
        bindTitle(phone)

        setListeners()
        authorise()
    }

    private fun setListeners() {

        binding.verifyAnotherEmail.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.verifySend.setOnClickListener {
            sendCode(binding.verifyInputCode.text.toString())
        }

        binding.verifyInputCode.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendCode(binding.verifyInputCode.text.toString())
            }
            return@setOnEditorActionListener true
        }
    }

    private fun bindTitle(phone: String?) {

        binding.verifyMessage.text = getString(R.string.verify_code_screen_message, phone.orEmpty())
    }

    private fun showProgress() {

        binding.verifySend.visibility = View.INVISIBLE
        binding.verifyProgress.visibility = View.VISIBLE
        binding.verifyProgress.animate().setDuration(300).alpha(1f).start()
    }

    private fun hideProgress() {

        binding.verifySend.visibility = View.VISIBLE
        binding.verifySend.alpha = 0f
        binding.verifySend.animate().setDuration(300).alpha(1f).start()
        binding.verifyProgress.visibility = View.INVISIBLE
    }

    /** authentication */
    private fun authorise() {

        if (phone.isEmpty()) {
            return
        }

        viewModel.authorise(
            phone,
            requireActivity(),
            object : PhoneAuthCallback {
                override fun onCodeSend(verificationId: String) {
                    Log.d(TAG, "authorise onCodeSend verificationId $verificationId")
                    this@LoginVerifyCodeFragment.verificationId = verificationId
                }

                override fun onCompleted(result: PhoneAuthCred<*>) {
                    Log.d(TAG, "authorise onCompleted")
                    viewModel.authoriseWithCred(result)
                }

                override fun onFailure(e: Exception) {
                    Log.d(TAG, "authorise onFailure ${e.message}")
                    loginFailure(e)
                }
            }
        )
    }

    private fun sendCode(code: String) {

        hideKeyboard()

        if (!validFields()) return

        showProgress()

        viewModel.sendCode(phone, code, verificationId).observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Log.d(TAG, "sendCode: onSuccess: $it")
                loginSuccess()
            }
            result.onFailure {
                Log.d(TAG, "sendCode: onFailure: $it")
                loginFailure(it)
            }
        }
    }

    /**login handlers*/
    private fun loginSuccess() {

        Log.d(TAG, "login: loginSuccess")
        hideProgress()

        startMainScreen()
    }

    private fun loginFailure(throwable: Throwable? = null) {

        if (throwable is AuthException) {
            Log.d(TAG, "login: AuthException ${throwable.errorCode}")
        }

        hideProgress()

        if (throwable is AuthException) {
            when (throwable.errorCode) {
                AuthErrorCode.INVALID_VERIFICATION_CODE -> showErrorMessage(R.string.auth_error_incorrect_verification_code)
                AuthErrorCode.NETWORK_EXCEPTION -> {
                    showErrorMessage(R.string.auth_error_network)
                    disableInputCode()
                }
                else -> showErrorMessage(R.string.error_unknown)
            }
        }
    }

    private fun disableInputCode() {

        binding.verifyInputCode.isEnabled = false
        binding.verifyInputCode.setText("")
    }

    private fun validFields(): Boolean {

        var valid = true

        val code = binding.verifyInputCode.text.toString()
        if (code.isBlank()) {
            valid = false
            showErrorMessage(R.string.auth_error_empty_verification_code)
        }

        return valid
    }

    private fun showErrorMessage(@StringRes id: Int) {
        showMessage(getString(id))
    }

    private fun startMainScreen() {

        findNavController().navigate(R.id.action_loginVerifyCodeFragment_to_mainFragment)
    }
}