package com.telematics.features.account.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.telematics.domain_.listener.UserUpdatedListener
import com.telematics.domain_.model.authentication.User
import com.telematics.features.account.R
import com.telematics.features.account.databinding.FragmentAccountBinding
import com.telematics.features.account.model.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : Fragment(), UserUpdatedListener {

    @Inject
    lateinit var accountViewModel: AccountViewModel

    private lateinit var binding: FragmentAccountBinding
    //var user = User()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAccountBinding.inflate(inflater, container, false)
        //binding.user = accountViewModel.getUser()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setListeners()

        lifecycleScope.launchWhenResumed {
            accountViewModel.user.collect { it ->
                when (it) {
                    is Resource.Success -> {
                        bindUser(it.data ?: User())
                    }
                    is Resource.Failure -> {
                        it.error
                    }
                }
            }
        }
        //val user = accountViewModel.getUser()
        //bindUser(user)
    }

    override fun onUserUpdated(user: User) {
        bindUser(user)
    }

    override fun onUserUpdateFailure() {
        //show error message
    }

    private fun setListeners() {

        binding.userInfoCard.accountDocumentItem.setOnClickListener {
            openProfileFragment()
        }

        binding.accountLogout.setOnClickListener {
            logout()
        }

        accountViewModel.setListener(null, this)
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
        //fix user nav_graph
        parentFragmentManager.beginTransaction()
            .replace(R.id.accountParent, ProfileFragment(), "ProfileFragment")
            .addToBackStack("ProfileFragment")
            .commit()
        //binding.root.findNavController().navigate(R.id.action_accountFragment_to_profileFragment
    }
}