package com.telematics.zenroad.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.telematics.authentication.exception.AuthErrorCode
import com.telematics.authentication.exception.AuthException
import com.telematics.domain.model.LoginType
import com.telematics.zenroad.R
import com.telematics.zenroad.databinding.LoginFragmentBinding
import com.telematics.zenroad.extention.isValidEmail
import com.telematics.zenroad.extention.setVisible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val TAG = "LoginFragment"

    companion object {

        const val PASSWORD_MIN_LENGTH = 4

        const val BUNDLE_LOGIN_KEY = "login_key"
        const val BUNDLE_PASSWORD_KEY = "password_key"
        const val BUNDLE_LOGIN_TYPE_KEY = "login_type_key"
    }

    @Inject
    lateinit var loginViewModel: LoginViewModel

    private lateinit var binding: LoginFragmentBinding

    private var loginType = LoginType.EMAIL

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        initScreen()
        mockFields()
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
    }

    private fun changeInputLoginType() {
        loginType = if (loginType == LoginType.EMAIL) LoginType.PHONE else LoginType.EMAIL
        initScreen()
    }

    private fun initScreen() {

        animateViews()

        when (loginType) {
            LoginType.EMAIL -> {
                //initInputField(loginInputEmail, regType)
                binding.loginInputEmailTill.setVisible(true)
                binding.loginInputPhoneTill.setVisible(null)
                binding.loginInputPasswordTill.setVisible(true)
                binding.loginUseEmailOrPhone.text = getString(R.string.sign_in)
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
                //initInputField(loginInputPhone, regType)
                binding.loginInputPhoneTill.setVisible(true)
                binding.loginInputEmailTill.setVisible(null)
                binding.loginInputPasswordTill.setVisible(null)
                binding.loginUseEmailOrPhone.text = getString(R.string.login_screen_use_phone)
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

        val duration = 500L
        val durationK = 100L

        binding.loginInputEmailTill.alpha = 0f
        binding.loginInputPhoneTill.alpha = 0f
        binding.loginChangeButton.alpha = 0f
        binding.loginUseEmailOrPhone.alpha = 0f

        binding.loginChangeButton.alpha = 0f

        binding.loginInputEmailTill.animate().alpha(1f).setDuration(duration + durationK * 1)
            .start()
        binding.loginInputPhoneTill.animate().alpha(1f).setDuration(duration + durationK * 1)
            .start()
        binding.loginChangeButton.animate().alpha(1f).setDuration(duration + durationK * 2).start()
        binding.loginUseEmailOrPhone.animate().alpha(1f).setDuration(duration + durationK * 2)
            .start()
        binding.loginChangeButton.animate().alpha(1f).setDuration(duration + durationK * 5).start()
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
        val phoneField = binding.loginInputPhone.text.toString()

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
            if (phoneField.isBlank()) {
                showLoginFailedMessage(R.string.auth_error_empty_phone)
                return false
            }

        return true
    }

    private fun showLoginFailedMessage(@StringRes id: Int) {
        showLoginFailedMessage(getString(id))
    }

    private fun showLoginFailedMessage(message: String) {
        Log.d(TAG, "message: $message")
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun getLoginField(): String {

        return when (loginType) {
            LoginType.EMAIL -> {
                binding.loginInputEmail.text.toString()
            }
            LoginType.PHONE -> {
                binding.loginInputPhone.text.toString()
            }
        }
    }

    private fun getPasswordField(): String {
        return binding.loginInputPassword.text.toString()
    }
    /** ↑ UI ↑ */

    /**↓login*/
    private fun login() {

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
                else -> showLoginFailedMessage(R.string.auth_error_unknown)
            }
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

        val bundle = bundleOf(BUNDLE_LOGIN_KEY to getLoginField())
        findNavController().navigate(R.id.action_loginFragment_to_loginVerifyCodeFragment, bundle)
    }

    private fun startRegistrationFragment() {

        val bundle = bundleOf(
            BUNDLE_LOGIN_KEY to getLoginField(),
            BUNDLE_PASSWORD_KEY to getPasswordField(),
            BUNDLE_LOGIN_TYPE_KEY to loginType
        )
        findNavController().navigate(R.id.action_loginFragment_to_registrationFragment, bundle)
    }

    private fun mockFields() {

        // FIXME: remove
        binding.loginInputPhone.setText("+79009057055")
        binding.loginInputEmail.setText("android@dev.com")
        binding.loginInputPassword.setText("123456")
    }
}