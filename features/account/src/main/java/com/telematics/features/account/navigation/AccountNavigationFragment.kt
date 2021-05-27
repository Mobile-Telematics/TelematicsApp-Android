package com.telematics.features.account.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.telematics.features.account.R
import com.telematics.features.account.databinding.AccountNavigationFragmentBinding


class AccountNavigationFragment : Fragment() {

    private lateinit var binding: AccountNavigationFragmentBinding
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AccountNavigationFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navController = Navigation.findNavController(binding.accountNavigation)
        navController.navigate(R.id.accountFragment)
    }
}