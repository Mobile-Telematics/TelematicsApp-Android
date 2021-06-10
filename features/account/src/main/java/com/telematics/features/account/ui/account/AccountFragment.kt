package com.telematics.features.account.ui.account

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.telematics.domain.model.authentication.User
import com.telematics.features.account.R
import com.telematics.features.account.databinding.FragmentAccountBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private val TAG = "AccountFragment"

    companion object {
        const val ACCOUNT_USER_KEY = "account_user_key"
        const val ACCOUNT_USER_BUNDLE_KEY = "account_user_bundle_key"
    }

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

        //get user after update
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(
            ACCOUNT_USER_KEY
        )?.observe(
            viewLifecycleOwner
        ) { result ->
            val user = result.getSerializable(ACCOUNT_USER_BUNDLE_KEY) as User
            bindUser(user)
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

        accountViewModel.logout().observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                openSplashFragment()
            }
            result.onFailure { }
        }
    }

    private fun openProfileFragment() {
        val uri = Uri.parse("telematics://profileFragment")
        findNavController().navigate(uri)
    }

    private fun openSplashFragment() {
        //val uri = Uri.parse("telematics://splashFragment")
        //findNavController().navigate(uri)
        popToRoot(findNavController())
    }

    private fun popToRoot(navController: NavController) {

        val mBackStackField by lazy {
            val field = NavController::class.java.getDeclaredField("mBackStack")
            field.isAccessible = true
            field
        }

        val arrayDeque =
            mBackStackField.get(navController) as java.util.ArrayDeque<NavBackStackEntry>
        val graph = arrayDeque.first.destination as NavGraph
        val rootDestinationId = graph.startDestination

        val navOptions = NavOptions.Builder()
            .setPopUpTo(rootDestinationId, true)
            .setLaunchSingleTop(true)
            .build()

        navController.navigate(rootDestinationId, null, navOptions)
    }
}