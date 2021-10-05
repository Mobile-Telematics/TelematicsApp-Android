package com.telematics.zenroad.ui.registration

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.findNavController
import com.telematics.authentication.exception.AuthErrorCode
import com.telematics.authentication.exception.AuthException
import com.telematics.content.utils.BaseFragment
import com.telematics.data.BuildConfig
import com.telematics.domain.model.LoginType
import com.telematics.zenroad.R
import com.telematics.zenroad.databinding.SignUpFragmentBinding
import com.telematics.zenroad.extention.isValidEmail
import com.telematics.zenroad.extention.setVisible
import com.telematics.zenroad.ui.login.LoginFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegistrationFragment : BaseFragment() {

    private val TAG = "RegistrationFragment"

    @Inject
    lateinit var viewModel: RegistrationViewModel

    private lateinit var binding: SignUpFragmentBinding

    private var loginType = LoginType.EMAIL
    private var countryCode = -1
    private val PASSWORD_MIN_LENGTH = LoginFragment.PASSWORD_MIN_LENGTH

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = SignUpFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val login = arguments?.getString(LoginFragment.BUNDLE_LOGIN_KEY) ?: ""
        val password = arguments?.getString(LoginFragment.BUNDLE_PASSWORD_KEY) ?: ""
        val loginType = arguments?.get(LoginFragment.BUNDLE_LOGIN_TYPE_KEY) as LoginType
        this.loginType = loginType

        Log.d(TAG, "onViewCreated: login:$login password:$password loginType:$loginType")

        setListeners()
        initScreen()

        bindFields(login, password, loginType)
    }

    private fun setListeners() {

        binding.registrationSend.setOnClickListener {
            registration()
        }

        binding.registrationSighIn.setOnClickListener {
            startLogin()
        }

        binding.registrationChangeButton.setOnClickListener {
            changeInputLoginType()
        }

        binding.registrationSend.isEnabled = binding.registrationPolicyCheck.isChecked
        val rawString =
            "<a href=\"${BuildConfig.PRIVACY_POLICY}\">${
                getString(
                    R.string.login_screen_policy
                )
            }</a>"
        binding.registrationPolicy.text =
            HtmlCompat.fromHtml(rawString, HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.registrationPolicy.movementMethod = LinkMovementMethod.getInstance()
        binding.registrationPolicy.setOnClickListener {
            binding.registrationPolicyCheck.isChecked = !binding.registrationPolicyCheck.isChecked
            binding.registrationSend.isEnabled = binding.registrationPolicyCheck.isChecked
            binding.registrationSighIn.isEnabled = binding.registrationPolicyCheck.isChecked
        }
        binding.registrationPolicyCheck.setOnClickListener {
            binding.registrationSend.isEnabled = binding.registrationPolicyCheck.isChecked
            binding.registrationSighIn.isEnabled = binding.registrationPolicyCheck.isChecked
        }
    }

    private fun initScreen() {

        animateViews()

        when (loginType) {
            LoginType.EMAIL -> {
                binding.registrationInputEmailTill.setVisible(true)
                binding.registrationInputPhoneTill.setVisible(null)
                binding.registrationInputPasswordTill.setVisible(true)
                binding.registrationChangeButton.text =
                    getString(R.string.login_screen_change_reg_type_phone_number)
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_login_phone)
                    ?.let { drw ->
                        binding.registrationChangeButton.setCompoundDrawablesWithIntrinsicBounds(
                            drw,
                            null,
                            null,
                            null
                        )
                    }
            }
            LoginType.PHONE -> {
                if (countryCode == -1) {
                    countryCode = binding.registrationInputPhoneCCP.selectedCountryCodeAsInt
                }
                binding.registrationInputPhoneCCP.setCountryForPhoneCode(countryCode)
                binding.registrationInputPhoneCCP.registerCarrierNumberEditText(binding.registrationInputPhone)
                binding.registrationInputPhoneTill.setVisible(true)
                binding.registrationInputEmailTill.setVisible(null)
                binding.registrationInputPasswordTill.setVisible(null)
                binding.registrationChangeButton.text =
                    getString(R.string.login_screen_change_reg_type_email)
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_login_email)
                    ?.let { drw ->
                        binding.registrationChangeButton.setCompoundDrawablesWithIntrinsicBounds(
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

        animateView(binding.registrationTitle, 1)
        animateView(binding.registrationInputEmailTill, 1)
        animateView(binding.registrationInputPhoneTill, 1)
        animateView(binding.registrationInputPasswordTill, 1)
        animateView(binding.registrationSend, 2)
        animateView(binding.registrationSighIn, 3)
        animateView(binding.registrationJoinVia, 3)
        animateView(binding.registrationChangeButton, 3)
        animateView(binding.registrationPolicyLayout, 4)
    }

    private fun bindFields(login: String, password: String, loginType: LoginType) {

        when (loginType) {
            LoginType.PHONE -> {
                binding.registrationInputPhoneCCP.fullNumber = login
            }
            LoginType.EMAIL -> {
                binding.registrationInputEmail.setText(login)
                binding.registrationInputPassword.setText(password)
            }
        }
    }

    private fun changeInputLoginType() {
        loginType = if (loginType == LoginType.EMAIL) LoginType.PHONE else LoginType.EMAIL
        initScreen()
    }

    private fun showProgress() {

        binding.registrationSend.visibility = View.INVISIBLE
        binding.registrationProgress.visibility = View.VISIBLE
        binding.registrationProgress.animate().setDuration(300).alpha(1f).start()
    }

    private fun hideProgress() {

        binding.registrationSend.visibility = View.VISIBLE
        binding.registrationSend.alpha = 0f
        binding.registrationSend.animate().setDuration(300).alpha(1f).start()
        binding.registrationProgress.visibility = View.INVISIBLE
    }

    private fun validFields(): Boolean {

        val passwordField = binding.registrationInputPassword.text.toString()
        val emailField = binding.registrationInputEmail.text.toString()

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
            if (!binding.registrationInputPhoneCCP.isValidFullNumber) {
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
                binding.registrationInputEmail.text.toString()
            }
            LoginType.PHONE -> {
                binding.registrationInputPhoneCCP.selectedCountryCodeWithPlus + binding.registrationInputPhone.text.toString()
            }
        }
    }

    private fun getPasswordField(): String {
        return binding.registrationInputPassword.text.toString()
    }

    private fun registration() {

        hideKeyboard()

        if (!validFields()) return

        if (loginType == LoginType.PHONE) {
            startVerifyCodeFragment()
            return
        }

        showProgress()

        viewModel.registration(
            getLoginField(),
            getPasswordField(),
            loginType
        ).observe(viewLifecycleOwner) { result ->
            result.onSuccess { isAuthorize ->
                Log.d(TAG, "registration: $isAuthorize")
                if (isAuthorize)
                    loginSuccess()
                else loginFailure()
            }
            result.onFailure {
                loginFailure(it)
            }
        }
    }

    /** login handlers */
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
                AuthErrorCode.EMAIL_ALREADY_IN_USE -> showLoginFailedMessage(R.string.auth_error_email_already_in_use)
                AuthErrorCode.INVALID_PASSWORD -> showLoginFailedMessage(R.string.auth_error_invalid_email_password)
                AuthErrorCode.NETWORK_EXCEPTION -> showLoginFailedMessage(R.string.auth_error_network)
                else -> showLoginFailedMessage(R.string.error_unknown)
            }
        }
    }

    /** navigation */
    private fun startVerifyCodeFragment() {

        countryCode = binding.registrationInputPhoneCCP.selectedCountryCodeAsInt

        val bundle = bundleOf(LoginFragment.BUNDLE_LOGIN_KEY to getLoginField())
        findNavController().navigate(
            R.id.action_registrationFragment_to_loginVerifyCodeFragment,
            bundle
        )
    }

    private fun startLogin() {

        findNavController().popBackStack()
    }

    private fun startMainScreen() {

        findNavController().navigate(R.id.action_registrationFragment_to_mainFragment)
    }
}