package com.telematics.features.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.telematics.features.account.databinding.FragmentAccountFeatureHostBinding

class AccountFeatureHost : Fragment() {

    private lateinit var binding: FragmentAccountFeatureHostBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountFeatureHostBinding.inflate(inflater, container, false)
        return binding.root
    }
}