package com.telematics.zenroad.ui.registration

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.telematics.authentication.exception.AuthErrorCode
import com.telematics.authentication.exception.AuthException
import com.telematics.domain.model.LoginType
import com.telematics.zenroad.R
import com.telematics.zenroad.databinding.RegistrationFragmentBinding
import com.telematics.zenroad.extention.isValidEmail
import com.telematics.zenroad.extention.setVisible
import com.telematics.zenroad.ui.login.LoginFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private val TAG = "RegistrationFragment"

    @Inject
    lateinit var viewModel: RegistrationViewModel

    private lateinit var binding: RegistrationFragmentBinding

    private var loginType = LoginType.EMAIL
    private val PASSWORD_MIN_LENGTH = LoginFragment.PASSWORD_MIN_LENGTH

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = RegistrationFragmentBinding.inflate(inflater, container, false)
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
    }

    private fun initScreen() {

        animateViews()

        when (loginType) {
            LoginType.EMAIL -> {
                //initInputField(loginInputEmail, regType)
                binding.registrationInputEmailTill.setVisible(true)
                binding.registrationInputPhoneTill.setVisible(null)
                binding.registrationInputPasswordTill.setVisible(true)
                binding.registrationUseEmailOrPhone.text = getString(R.string.sign_up)
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
                binding.registrationInputPhoneTill.setVisible(true)
                binding.registrationInputEmailTill.setVisible(null)
                binding.registrationInputPasswordTill.setVisible(null)
                binding.registrationUseEmailOrPhone.text =
                    getString(R.string.login_screen_use_phone)
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

        val duration = 500L
        val durationK = 100L

        binding.registrationInputEmailTill.alpha = 0f
        binding.registrationInputPhoneTill.alpha = 0f
        binding.registrationChangeButton.alpha = 0f
        binding.registrationUseEmailOrPhone.alpha = 0f

        binding.registrationChangeButton.alpha = 0f

        binding.registrationInputEmailTill.animate().alpha(1f).setDuration(duration + durationK * 1)
            .start()
        binding.registrationInputPhoneTill.animate().alpha(1f).setDuration(duration + durationK * 1)
            .start()
        binding.registrationChangeButton.animate().alpha(1f).setDuration(duration + durationK * 2)
            .start()
        binding.registrationUseEmailOrPhone.animate().alpha(1f)
            .setDuration(duration + durationK * 2)
            .start()
        binding.registrationChangeButton.animate().alpha(1f).setDuration(duration + durationK * 5)
            .start()
    }

    private fun bindFields(login: String, password: String, loginType: LoginType) {

        when (loginType) {
            LoginType.PHONE -> binding.registrationInputPhone.setText(login)
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
        val phoneField = binding.registrationInputPhone.text.toString()

        if (loginType == LoginType.EMAIL) {

            if (emailField.isBlank()) {
                showLoginFailedMessage("Email field must be filled.")
                return false
            } else {
                if (!emailField.isValidEmail()) {
                    showLoginFailedMessage("Email address isn’t correct.")
                    return false
                }
            }

            if (passwordField.isBlank()) {
                showLoginFailedMessage("Password field must be filled.")
                return false
            }

            if (passwordField.length <= PASSWORD_MIN_LENGTH) {
                showLoginFailedMessage("password must be at least $PASSWORD_MIN_LENGTH characters")
                return false
            }
        }

        if (loginType == LoginType.PHONE)
            if (phoneField.isBlank()) {
                showLoginFailedMessage("Phone field must be filled.")
                return false
            }

        return true
    }

    private fun showLoginFailedMessage(message: String) {
        Log.d(TAG, "message: $message")
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun getLoginField(): String {

        return when (loginType) {
            LoginType.EMAIL -> {
                binding.registrationInputEmail.text.toString()
            }
            LoginType.PHONE -> {
                binding.registrationInputPhone.text.toString()
            }
        }
    }

    private fun getPasswordField(): String {
        return binding.registrationInputPassword.text.toString()
    }

    private fun registration() {

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
                AuthErrorCode.EMAIL_ALREADY_IN_USE -> showLoginFailedMessage("The email address is already in use by another account")
                AuthErrorCode.INVALID_PASSWORD -> showLoginFailedMessage("Invalid email or password")
                else -> showLoginFailedMessage("Unknown error")
            }
        }
    }

    /** navigation */
    private fun startVerifyCodeFragment() {

        val bundle = bundleOf("phone" to getLoginField())
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