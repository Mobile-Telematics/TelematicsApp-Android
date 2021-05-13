package com.telematics.zenroad.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.telematics.data_.model.SessionData
import com.telematics.data_.model.login.LoginType
import com.telematics.data_.utils.Resource
import com.telematics.zenroad.MainActivity
import com.telematics.zenroad.R
import com.telematics.zenroad.databinding.LoginActivityBinding
import com.telematics.zenroad.extention.isValidEmail
import com.telematics.zenroad.extention.setVisible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: LoginActivityBinding

    private var loginType = LoginType.EMAIL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginSend.setOnClickListener {

            if (!validFields()) return@setOnClickListener

            val login = when (loginType) {
                LoginType.EMAIL -> {
                    binding.loginInputEmail.text.toString()
                }
                LoginType.PHONE -> {
                    binding.loginInputPhone.text.toString()
                }
            }
            loginViewModel.login(
                login,
                binding.loginInputPassword.text.toString(),
                loginType
            )
        }

        binding.loginChangeButton.setOnClickListener {
            changeInputLoginType()
        }

        loginViewModel.loginState.observe(this, {
            when (it) {
                is Resource.Failure -> {
                    showLoginFailed("Incorrect login or password")
                    hideProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
                is Resource.Success<SessionData> -> {
                    hideProgress()
                    startMainActivity()
                }
            }
        })

        initScreen()

        mockFields()
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
                binding.loginUseEmailOrPhone.text = getString(R.string.login_screen_use_email)
                binding.loginChangeButton.text =
                    getString(R.string.login_screen_change_reg_type_phone_number)
                ContextCompat.getDrawable(this, R.drawable.ic_login_phone)
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
                binding.loginUseEmailOrPhone.text = getString(R.string.login_screen_use_phone)
                //binding.loginInputPhoneCCP.registerCarrierNumberEditText(loginInputPhone)
                //val typeFace = Typeface.createFromAsset(context!!.assets,getString(R.string.font_main_semibold))
                //binding.loginInputPhoneCCP.setTypeFace(typeFace)
                binding.loginChangeButton.text =
                    getString(R.string.login_screen_change_reg_type_email)
                ContextCompat.getDrawable(this, R.drawable.ic_login_email)
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

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }

    private fun validFields(): Boolean {

        var valid = true

        if (loginType == LoginType.EMAIL) {
            val email = binding.loginInputEmail.text.toString()

            if (email.isBlank()) {
                showLoginFailed("Email field must be filled.")
                valid = false
            } else {
                if (!email.isValidEmail()) {
                    showLoginFailed("Email address isn’t correct.")
                    valid = false
                }
            }
        }

        if (loginType == LoginType.PHONE)
            if (binding.loginInputPhone.text.isNullOrBlank()) {
                showLoginFailed("Phone field must be filled.")
                valid = false
            }

        if (binding.loginInputPassword.text.isNullOrBlank()) {
            showLoginFailed("Password field must be filled.")
            valid = false
        }

        return valid
    }

    private fun showLoginFailed(message: String) {
        Log.d("LoginActivity", message)
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }


    private fun mockFields() {

        // FIXME: remove
        binding.loginInputPhone.setText("+6281338240831")
        binding.loginInputEmail.setText("notogel205@yncyjs.com")
        binding.loginInputPassword.setText("123456")
    }
}