package com.telematics.features.account.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.telematics.content.utils.BaseFragment
import com.telematics.domain.model.authentication.User
import com.telematics.features.account.R
import com.telematics.features.account.databinding.FragmentProfileBinding
import com.telematics.features.account.model.DatePickerDialog
import com.telematics.features.account.ui.account.AccountFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment() {

    @Inject
    lateinit var profileViewModel: ProfileViewModel
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

        setBackPressedCallback()
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

        binding.accountWizardEmailEdit.setOnClickListener {

        }

        profileViewModel.getUser().observe(viewLifecycleOwner) { result ->
            result.onSuccess { user ->
                bindUser(user)
            }
            result.onFailure {
                bindUser(User())
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

        profileViewModel.updateUser(newUser)

        finish(newUser)
    }

    private fun showErrorMessage(@StringRes id: Int) {
        showErrorMessage(getString(id))
    }

    private fun showErrorMessage(message: String) {
        showMessage(message)
    }

    private fun finish(newUser: User) {

        hideKeyboard()

        val bundle = bundleOf(AccountFragment.ACCOUNT_USER_BUNDLE_KEY to newUser)
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            AccountFragment.ACCOUNT_USER_KEY,
            bundle
        )
        findNavController().popBackStack()
    }
}