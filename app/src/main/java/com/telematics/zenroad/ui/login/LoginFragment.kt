package com.telematics.zenroad.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.telematics.authentication.data.Authentication
import com.telematics.authentication.extention.observeOnce
import com.telematics.domain.error.ErrorCode
import com.telematics.domain.model.LoginType
import com.telematics.domain.model.SessionData
import com.telematics.zenroad.R
import com.telematics.zenroad.databinding.LoginActivityBinding
import com.telematics.zenroad.extention.isValidEmail
import com.telematics.zenroad.extention.setVisible
import com.telematics.zenroad.ui.login.verification_code.LoginVerifyCodeFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val TAG = "LoginFragment"

    @Inject
    lateinit var loginViewModel: LoginViewModel

    private lateinit var binding: LoginActivityBinding

    private var loginType = LoginType.EMAIL
    private val PASSWORD_LIMIT = 4

    private lateinit var verifyCodeActivityResult: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = LoginActivityBinding.inflate(inflater, container, false)
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
                binding.loginUseEmailOrPhone.text = getString(R.string.login_screen_use_email)
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
                //binding.loginInputPhoneCCP.registerCarrierNumberEditText(loginInputPhone)
                //val typeFace = Typeface.createFromAsset(context!!.assets,getString(R.string.font_main_semibold))
                //binding.loginInputPhoneCCP.setTypeFace(typeFace)
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

        //binding.loginJoinVia.alpha = 0f
        //binding.loginJoinVia.animate().alpha(1f).setDuration(duration + durationK * 4).start()
        //binding.loginSend.alpha = 0f
        //binding.loginSend.animate().alpha(1f).setDuration(duration + durationK * 3).start()
        //binding.loginCommonLogo.alpha = 0f
        //binding.loginCommonLogo.animate().alpha(1f).setDuration(duration + durationK * 3).start()
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

        var valid = true

        if (loginType == LoginType.EMAIL) {

            if (emailField.isBlank()) {
                showLoginFailedMessage("Email field must be filled.")
                valid = false
            } else {
                if (!emailField.isValidEmail()) {
                    showLoginFailedMessage("Email address isn’t correct.")
                    valid = false
                }
            }
        }

        if (loginType == LoginType.PHONE)
            if (phoneField.isBlank()) {
                showLoginFailedMessage("Phone field must be filled.")
                valid = false
            }

        if (passwordField.isBlank() && passwordField.length >= PASSWORD_LIMIT) {
            showLoginFailedMessage("Password field must be filled.")
            valid = false
        }

        if (!valid) hideProgress()

        return valid
    }

    private fun showLoginFailedMessage(message: String) {
        Log.d("LoginActivity", "message: $message")
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

    private fun login() {

        showProgress()

        if (!validFields()) return


        if (loginType == LoginType.PHONE)
            loginViewModel.isNeedPhoneVerify().observeOnce(viewLifecycleOwner) { result ->
                result.onSuccess { needPhoneVerification ->
                    if (needPhoneVerification) {
                        startVerifyCodeFragment()
                    }
                }
            }

        val loginObserve = loginViewModel.login(
            getLoginField(),
            getPasswordField(),
            loginType,
            requireActivity()
        )
        loginObserve.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                onLoginSuccess(it)
            }
            result?.onFailure {
                onLoginFailure(it as Authentication.AuthException)
            }
        }
    }

    private fun onLoginSuccess(sessionData: SessionData) {
        Log.d(TAG, "login: onSuccess")
        hideProgress()
    }

    private fun startVerifyCodeFragment() {

        val bundle = bundleOf("phone" to getLoginField())
        findNavController().navigate(R.id.action_loginFragment_to_loginVerifyCodeFragment, bundle)
    }

    private fun onLoginFailure(error: Authentication.AuthException) {
        Log.d(TAG, "login: onFailure ${error.errorCode}")
        if (error.errorCode != ErrorCode.EMPTY_SESSION || error.errorCode != ErrorCode.NONE)
            hideProgress()

        if (error.errorCode == ErrorCode.USER_NOT_EXIST)
            showRegistrationDialog()
    }

    private fun showRegistrationDialog() {

        AlertDialog.Builder(requireContext())
            .setMessage(R.string.dialog_registration_confirm_proceed)
            .setPositiveButton(R.string.dialog_confirm) { d, _ ->
                registration()
                d.cancel()
            }
            .setNegativeButton(R.string.dialog_cancel) { d, _ ->
                d.cancel()
            }
            .setCancelable(true)
            .show()
    }

    private fun registration() {

        loginViewModel.registration(
            getLoginField(),
            getPasswordField(),
            loginType
        )
    }

    private fun mockFields() {

        // FIXME: remove
        binding.loginInputPhone.setText("+79009057055")
        binding.loginInputEmail.setText("android_06@dev.com")
        binding.loginInputPassword.setText("123456")
    }
}