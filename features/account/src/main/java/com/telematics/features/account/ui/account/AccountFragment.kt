package com.telematics.features.account.ui.account

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.telematics.domain.model.authentication.User
import com.telematics.features.account.R
import com.telematics.features.account.databinding.FragmentAccountBinding
import com.telematics.features.account.ui.profile.ProfileFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : Fragment() {

    @Inject
    lateinit var accountViewModel: AccountViewModel

    private lateinit var binding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setListeners()
    }

    fun onUserUpdated(user: User) {
        bindUser(user)
    }

    private fun setListeners() {

        binding.userInfoCard.accountDocumentItem.setOnClickListener {
            openProfileFragment()
        }

        binding.accountLogout.setOnClickListener {
            logout()
        }

        accountViewModel.getUser().observe(viewLifecycleOwner) { result ->
            result.onSuccess { user ->
                bindUser(user)
            }
            result.onFailure {
                bindUser(User())
            }
        }
    }

    private fun bindUser(user: User) {

        val userName = "${user.firstName.orEmpty()} ${user.lastName.orEmpty()}"
        binding.accountUserName.text = userName

        if (user.phone.isNullOrBlank()) {
            binding.accountLoginField.text = user.email
            binding.accountLoginType.setText(R.string.account_email)
        } else {
            binding.accountLoginField.text = user.phone
            binding.accountLoginType.setText(R.string.account_phone)
        }

        if (user.isCompleted()) {
            binding.accountInfoArea.visibility = View.VISIBLE
            binding.accountCompleteBtn.visibility = View.GONE
        } else {
            binding.accountInfoArea.visibility = View.GONE
            binding.accountCompleteBtn.visibility = View.VISIBLE
        }

        val birthdayStr =
            if (user.birthday.isNullOrEmpty()) resources.getString(R.string.account_not_specified) else user.birthday
        binding.userInfoCard.birthDay.text = birthdayStr

        val addressStr =
            if (user.address.isNullOrEmpty()) resources.getString(R.string.account_not_specified) else user.address
        binding.userInfoCard.address.text = addressStr
    }

    private fun logout() {

        accountViewModel.logout()
    }

    private fun openProfileFragment() {
        val uri = Uri.parse("telematics://profileFragment")
        findNavController().navigate(uri)
    }
}