package com.telematics.zenroad.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.telematics.features.account.model.Resource
import com.telematics.features.account.ui.AccountViewModel
import com.telematics.zenroad.R
import com.telematics.zenroad.databinding.ActivityVerifyCodeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class LoginVerifyCodeActivity : AppCompatActivity() {

    companion object {
        const val RESULT_CODE_KEY = "CODE_KEY"
    }

    @Inject
    lateinit var accountViewModel: AccountViewModel

    private lateinit var binding: ActivityVerifyCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerifyCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launchWhenResumed {
            accountViewModel.user.collect {
                when (it) {
                    is Resource.Success -> {
                        bindTitle(it.data?.phone)
                    }
                    is Resource.Failure -> {
                        it.error
                    }
                }
            }
        }

        setListeners()
    }

    private fun setListeners() {

        binding.verifyAnotherEmail.setOnClickListener {
            setResult(null)
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

        val intent = Intent()
        val resultCode = code?.let {
            intent.putExtra(RESULT_CODE_KEY, code)
            Activity.RESULT_OK
        } ?: Activity.RESULT_CANCELED
        setResult(resultCode, intent)
        finish()
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