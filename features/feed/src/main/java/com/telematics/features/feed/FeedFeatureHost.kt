package com.telematics.features.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.telematics.feed.databinding.FragmentFeedFeatureHostBinding

class FeedFeatureHost : Fragment() {

    lateinit var binding: FragmentFeedFeatureHostBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedFeatureHostBinding.inflate(inflater, container, false)
        return binding.root
    }
}