package com.telematics.features.account.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.telematics.domain_.model.authentication.User
import com.telematics.features.account.R
import com.telematics.features.account.databinding.FragmentProfileBinding
import com.telematics.features.account.model.DatePickerDialog
import com.telematics.features.account.model.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    @Inject
    lateinit var accountViewModel: AccountViewModel
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        lifecycleScope.launchWhenResumed {
            accountViewModel.user.collect {
                when (it) {
                    is Resource.Success -> {
                        it.data
                        bindUser(it.data ?: User())
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

        binding.accountWizardSaveBtn.setOnClickListener {
            saveUser()
        }

        binding.accountWizardBirthday.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                DatePickerDialog().create(
                    binding.accountWizardBirthday,
                    binding.accountWizardProfileFocusView,
                    getString(R.string.acc_wizard_birthday),
                    binding.accountWizardAddress,
                    maxDate = Date().time
                )
            }
        }
    }

    private fun bindUser(user: User) {

        if (user.email.isNullOrBlank()) {
            binding.accountWizardEmail.isEnabled = true
        }

        if (user.phone.isNullOrBlank()) {
            binding.accountWizardPhone.isEnabled = true
        }

        binding.accountWizardEmail.setText(user.email.orEmpty())
        binding.accountWizardPhone.setText(user.phone.orEmpty())
        binding.accountWizardName.setText(user.firstName.orEmpty())
        binding.accountWizardFamilyName.setText(user.lastName.orEmpty())
        binding.accountWizardBirthday.setText(user.birthday.orEmpty())
        binding.accountWizardAddress.setText(user.address.orEmpty())
        binding.accountClientId.setText(user.clientId.orEmpty())
    }

    private fun saveUser() {

        val email = binding.accountWizardEmail.text.toString()
        //todo need check phone
        val phone = binding.accountWizardPhone.text.toString()

        val firstName = binding.accountWizardName.text.toString()
        val lastName = binding.accountWizardFamilyName.text.toString()

        //check first name field
        if (firstName.isBlank()) {
            showErrorMessage(R.string.account_screen_first_name_invalid)
            return
        }
        //check last name field
        if (lastName.isBlank()) {
            showErrorMessage(R.string.account_screen_last_name_invalid)
            return
        }

        val birthday = binding.accountWizardBirthday.text.toString()
        val address = binding.accountWizardAddress.text.toString()
        val clientId = binding.accountClientId.text.toString()

        val newUser = User().apply {
            this.email = email
            this.phone = phone
            this.firstName = firstName
            this.lastName = lastName
            this.birthday = birthday
            this.address = address
            this.clientId = clientId
        }

        accountViewModel.updateUser(newUser)

        finish()
    }

    private fun showErrorMessage(@StringRes id: Int) {
        showErrorMessage(getString(id))
    }

    private fun showErrorMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun finish() {
        parentFragmentManager.popBackStack()
    }
}