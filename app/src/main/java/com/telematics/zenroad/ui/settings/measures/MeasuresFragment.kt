package com.telematics.zenroad.ui.settings.measures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.telematics.content.utils.BaseFragment
import com.telematics.domain.model.measures.DateMeasure
import com.telematics.domain.model.measures.DistanceMeasure
import com.telematics.domain.model.measures.TimeMeasure
import com.telematics.zenroad.databinding.MeasuresFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MeasuresFragment : BaseFragment() {

    lateinit var binding: MeasuresFragmentBinding

    @Inject
    lateinit var measuresViewModel: MeasuresViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MeasuresFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeMeasures()
        setListeners()
    }

    private fun setListeners() {

        binding.measuresBack.setOnClickListener {
            onBackPressed()
        }

        binding.kmBtn.setOnClickListener { measuresViewModel.setDistanceMeasure(DistanceMeasure.KM) }
        binding.miBtn.setOnClickListener { measuresViewModel.setDistanceMeasure(DistanceMeasure.MI) }

        binding.ddMmButton.setOnClickListener { measuresViewModel.setDateMeasure(DateMeasure.DD_MM) }
        binding.mmDdButton.setOnClickListener { measuresViewModel.setDateMeasure(DateMeasure.MM_DD) }

        binding.btn24.setOnClickListener { measuresViewModel.setTimeMeasure(TimeMeasure.H24) }
        binding.btn12.setOnClickListener { measuresViewModel.setTimeMeasure(TimeMeasure.H12) }
    }

    private fun observeMeasures() {

        measuresViewModel.getDateMeasure().observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                showDate(it)
            }
        }
        measuresViewModel.getDistanceMeasure().observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                showDistance(it)
            }
        }
        measuresViewModel.getTimeMeasure().observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                showTime(it)
            }
        }
    }

    private fun showDate(dateMeasure: DateMeasure) {

        when (dateMeasure) {
            DateMeasure.DD_MM -> {
                binding.ddMmButton.isChecked = true
            }
            DateMeasure.MM_DD -> {
                binding.mmDdButton.isChecked = true
            }
        }
    }

    private fun showDistance(distanceMeasure: DistanceMeasure) {

        when (distanceMeasure) {
            DistanceMeasure.KM -> {
                binding.kmBtn.isChecked = true
            }
            DistanceMeasure.MI -> {
                binding.miBtn.isChecked = true
            }
        }
    }

    private fun showTime(timeMeasure: TimeMeasure) {

        when (timeMeasure) {
            TimeMeasure.H24 -> {
                binding.btn24.isChecked = true
            }
            TimeMeasure.H12 -> {
                binding.btn12.isChecked = true
            }
        }
    }
}