package com.telematics.features.reward

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgument
import androidx.navigation.fragment.NavHostFragment
import com.telematics.reward.R
import com.telematics.reward.databinding.FragmentRewardFeatureHostBinding

class RewardFeatureHost : Fragment() {

    companion object {
        const val NAV_TO_STREAKS = "streaks"
        fun createFragment(toStreaks: Boolean) = RewardFeatureHost().apply {
            arguments = bundleOf(NAV_TO_STREAKS to toStreaks)
        }
    }

    private lateinit var binding: FragmentRewardFeatureHostBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRewardFeatureHostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initNavGraph()
    }

    private fun initNavGraph() {

        val toStreaks = arguments?.getBoolean(NAV_TO_STREAKS, false) ?: false
        val navArgument = NavArgument.Builder().setDefaultValue(toStreaks).build()
        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.reward_nav_host) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.reward_nav_graph)
        graph.addArgument(NAV_TO_STREAKS, navArgument)
        navHostFragment.navController.graph = graph
    }
}