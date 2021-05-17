package com.telematics.features.account.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.telematics.features.account.databinding.FragmentAccountBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//@AndroidEntryPoint
class AccountFragment : Fragment() {

//    @Inject
//    lateinit var sessionRepository: SessionRepo

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
        arguments?.getInt("position")

        binding.accountLogout.setOnClickListener {
            Toast.makeText(view.context, "accountLogout", Toast.LENGTH_SHORT).show()
            logout()
        }
    }

    private fun logout() {
        CoroutineScope(Dispatchers.IO).launch {
            //sessionRepository.clearSession()

            //val sessionData = sessionRepository.getSession()
//            Log.d("MainActivity", "refreshToken: " + sessionData.refreshToken)
//            Log.d("MainActivity", "accessToken: " + sessionData.accessToken)
//            Log.d("MainActivity", "deviceToken: " + sessionData.deviceToken)
//            Log.d("MainActivity", "expiresIn: " + sessionData.expiresIn.toString())
        }
    }
}