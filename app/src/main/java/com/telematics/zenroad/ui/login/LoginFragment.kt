package com.telematics.zenroad.ui.login

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.telematics.authentication.exception.AuthErrorCode
import com.telematics.authentication.exception.AuthException
import com.telematics.content.utils.BaseFragment
import com.telematics.data.BuildConfig
import com.telematics.domain.model.LoginType
import com.telematics.zenroad.R
import com.telematics.zenroad.databinding.SignInFragmentBinding
import com.telematics.zenroad.extention.isValidEmail
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    private val TAG = "LoginFragment"

    companion object {

        const val PASSWORD_MIN_LENGTH = 4

        const val BUNDLE_LOGIN_KEY = "login_key"
        const val BUNDLE_PASSWORD_KEY = "password_key"
        const val BUNDLE_LOGIN_TYPE_KEY = "login_type_key"
    }

    @Inject
    lateinit var loginViewModel: LoginViewModel

    private lateinit var binding: SignInFragmentBinding

    private var loginType = LoginType.EMAIL
    private var countryCode = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = SignInFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWhiteNavigationBar()

        setListeners()
        initScreen()
    }

    private fun setListeners() {

        binding.loginSend.setOnClickListener {
            login()
        }

        binding.loginChangeButton.setOnClickListener {
            changeInputLoginType()
        }

        binding.loginRegistration.setOnClickListener {
            startRegistrationFragment()
        }

        binding.loginSend.isEnabled = binding.loginPolicyCheck.isChecked
        val rawString =
            "<a href=\"${BuildConfig.PRIVACY_POLICY}\">${
                getString(
                    R.string.login_screen_policy
                )
            }</a>"
        binding.loginPolicy.text = HtmlCompat.fromHtml(rawString, HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.loginPolicy.movementMethod = LinkMovementMethod.getInstance()
        binding.loginPolicy.setOnClickListener {
            binding.loginPolicyCheck.isChecked = !binding.loginPolicyCheck.isChecked
            binding.loginSend.isEnabled = binding.loginPolicyCheck.isChecked
            binding.loginRegistration.isEnabled = binding.loginPolicyCheck.isChecked
        }
        binding.loginPolicyCheck.setOnClickListener {
            binding.loginSend.isEnabled = binding.loginPolicyCheck.isChecked
            binding.loginRegistration.isEnabled = binding.loginPolicyCheck.isChecked
        }

        binding.loginInputPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                login()
            }
            return@setOnEditorActionListener true
        }
    }

    private fun changeInputLoginType() {
        loginType = if (loginType == LoginType.EMAIL) LoginType.PHONE else LoginType.EMAIL
        initScreen()
    }

    private fun initScreen() {

        animateViews()

        when (loginType) {
            LoginType.EMAIL -> {
                binding.loginInputEmailTill.isVisible = true
                binding.loginInputPhoneTill.isVisible = false
                binding.loginInputPasswordTill.isVisible = true
                binding.loginChangeButton.text =
                    getString(R.string.login_screen_change_reg_type_phone_number)
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_login_phone)
                    ?.let { drw ->
                        binding.loginChangeButton.setCompoundDrawablesWithIntrinsicBounds(
                            drw,
                            null,
                            null,
                            null
                        )
                    }
            }
            LoginType.PHONE -> {
                if (countryCode == -1) {
                    countryCode = binding.loginInputPhoneCCP.selectedCountryCodeAsInt
                }
                binding.loginInputPhoneCCP.setCountryForPhoneCode(countryCode)
                binding.loginInputPhoneCCP.registerCarrierNumberEditText(binding.loginInputPhone)
                binding.loginInputPhoneTill.isVisible = true
                binding.loginInputEmailTill.isVisible = false
                binding.loginInputPasswordTill.isVisible = false
                binding.loginChangeButton.text =
                    getString(R.string.login_screen_change_reg_type_email)
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_login_email)
                    ?.let { drw ->
                        binding.loginChangeButton.setCompoundDrawablesWithIntrinsicBounds(
                            drw,
                            null,
                            null,
                            null
                        )
                    }
            }
        }
    }

    private fun animateViews() {

        fun animateView(view: View, index: Int = 1) {

            val duration = 500L
            val durationK = 100L
            val d = duration + durationK * index

            view.alpha = 0f
            view.animate().alpha(1f).setDuration(d)
                .start()
        }

        animateView(binding.loginTitle, 1)
        animateView(binding.loginInputEmailTill, 1)
        animateView(binding.loginInputPhoneTill, 1)
        animateView(binding.loginInputPasswordTill, 1)
        animateView(binding.loginSend, 2)
        animateView(binding.loginRegistration, 3)
        animateView(binding.loginJoinVia, 3)
        animateView(binding.loginChangeButton, 3)
        animateView(binding.loginPolicyLayout, 4)
    }

    private fun showProgress() {

        binding.loginSend.visibility = View.INVISIBLE
        binding.loginProgress.visibility = View.VISIBLE
        binding.loginProgress.animate().setDuration(300).alpha(1f).start()
    }

    private fun hideProgress() {

        binding.loginSend.visibility = View.VISIBLE
        binding.loginSend.alpha = 0f
        binding.loginSend.animate().setDuration(300).alpha(1f).start()
        binding.loginProgress.visibility = View.INVISIBLE
    }

    private fun validFields(): Boolean {

        val passwordField = binding.loginInputPassword.text.toString()
        val emailField = binding.loginInputEmail.text.toString()

        if (loginType == LoginType.EMAIL) {

            if (emailField.isBlank()) {
                showLoginFailedMessage(R.string.auth_error_empty_email)
                return false
            } else {
                if (!emailField.isValidEmail()) {
                    showLoginFailedMessage(R.string.auth_error_incorrect_email)
                    return false
                }
            }

            if (passwordField.isBlank()) {
                showLoginFailedMessage(R.string.auth_error_empty_password)
                return false
            }

            if (passwordField.length <= PASSWORD_MIN_LENGTH) {
                showLoginFailedMessage(
                    getString(
                        R.string.auth_error_short_password,
                        PASSWORD_MIN_LENGTH
                    )
                )
                return false
            }
        }

        if (loginType == LoginType.PHONE)
            if (!binding.loginInputPhoneCCP.isValidFullNumber) {
                showLoginFailedMessage(R.string.login_screen_phone_invalid)
                return false
            }

        return true
    }

    private fun showLoginFailedMessage(@StringRes id: Int) {
        showLoginFailedMessage(getString(id))
    }

    private fun showLoginFailedMessage(message: String) {
        Log.d(TAG, "message: $message")
        showMessage(message)
    }

    private fun getLoginField(): String {

        return when (loginType) {
            LoginType.EMAIL -> {
                binding.loginInputEmail.text.toString()
            }
            LoginType.PHONE -> {
                binding.loginInputPhoneCCP.selectedCountryCodeWithPlus + binding.loginInputPhone.text.toString()
            }
        }
    }

    private fun getPasswordField(): String {
        return binding.loginInputPassword.text.toString()
    }
    /** ↑ UI ↑ */

    /**↓login*/
    private fun login() {

        hideKeyboard()

        if (!validFields()) return

        showProgress()

        when (loginType) {
            LoginType.PHONE -> startVerifyCodeFragment()
            LoginType.EMAIL -> loginViewModel.authorize(
                getLoginField(),
                getPasswordField()
            ).observe(viewLifecycleOwner) { result ->
                result.onSuccess { isAuthorize ->
                    Log.d(TAG, "login: $isAuthorize")
                    if (isAuthorize)
                        loginSuccess()
                    else loginFailure()
                }
                result.onFailure {
                    loginFailure(it)
                }
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
                AuthErrorCode.USER_NOT_EXIST -> showRegistrationDialog()
                AuthErrorCode.EMPTY_DEVICE_TOKEN -> showRegistrationDialog()
                AuthErrorCode.INVALID_PASSWORD -> showLoginFailedMessage(R.string.auth_error_invalid_email_password)
                AuthErrorCode.NETWORK_EXCEPTION -> showLoginFailedMessage(R.string.auth_error_network)
                else -> showLoginFailedMessage(R.string.error_unknown)
            }
        } else {
            showLoginFailedMessage(R.string.error_unknown)
        }
    }

    /**navigation*/
    private fun showRegistrationDialog() {

        AlertDialog.Builder(requireContext())
            .setMessage(R.string.dialog_registration_confirm_proceed)
            .setPositiveButton(R.string.dialog_confirm) { d, _ ->
                startRegistrationFragment()
                d.cancel()
            }
            .setNegativeButton(R.string.dialog_cancel) { d, _ ->
                d.cancel()
            }
            .setCancelable(true)
            .show()
    }

    private fun startMainScreen() {

        findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
    }

    private fun startVerifyCodeFragment() {

        countryCode = binding.loginInputPhoneCCP.selectedCountryCodeAsInt

        val bundle = bundleOf(BUNDLE_LOGIN_KEY to getLoginField())
        findNavController().navigate(R.id.action_loginFragment_to_loginVerifyCodeFragment, bundle)
    }

    private fun startRegistrationFragment() {

        countryCode = binding.loginInputPhoneCCP.selectedCountryCodeAsInt

        val bundle = bundleOf(
            BUNDLE_LOGIN_KEY to getLoginField(),
            BUNDLE_PASSWORD_KEY to getPasswordField(),
            BUNDLE_LOGIN_TYPE_KEY to loginType
        )
        findNavController().navigate(R.id.action_loginFragment_to_registrationFragment, bundle)
    }
}