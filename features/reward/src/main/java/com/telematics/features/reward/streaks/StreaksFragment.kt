package com.telematics.features.reward.streaks

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.telematics.content.utils.BaseFragment
import com.telematics.domain.model.reward.Streak
import com.telematics.reward.databinding.FragmentStreaksBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StreaksFragment : BaseFragment() {

    @Inject
    lateinit var streaksViewModel: StreaksViewModel

    private lateinit var binding: FragmentStreaksBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStreaksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        init()
    }

    private fun setListeners() {

        val swipeRefreshLayout = binding.streaksSwipeRefresh
        swipeRefreshLayout.setOnRefreshListener {

            Handler().postDelayed({
                swipeRefreshLayout.isRefreshing = false
            }, 1000)

            init()
        }
    }

    private fun init() {

        binding.root.apply {
            alpha = 0f
            animate().setDuration(400).alpha(1f).start()
        }

        binding.streaksRecycler.layoutManager = LinearLayoutManager(requireContext())

        observeStreaksData()
    }

    private fun observeStreaksData() {

        streaksViewModel.getStreaksData().observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                showData(it)
            }
        }
    }

    private fun showData(list: List<Streak>) {

        binding.streaksRecycler.adapter = StreaksRecyclerAdapter(list)
    }
}