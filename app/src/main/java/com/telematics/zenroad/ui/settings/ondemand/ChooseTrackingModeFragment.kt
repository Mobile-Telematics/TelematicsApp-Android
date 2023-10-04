package com.telematics.zenroad.ui.settings.ondemand

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.telematics.content.utils.BaseFragment
import com.telematics.domain.model.on_demand.OnDemandJobState
import com.telematics.domain.model.on_demand.TrackingState
import com.telematics.zenroad.R
import com.telematics.zenroad.databinding.FragmentChooseTrakingModeBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChooseTrackingModeFragment : BaseFragment() {

    lateinit var binding: FragmentChooseTrakingModeBinding

    @Inject
    lateinit var viewModel: ChooseTrackingModeViewModel

    private var tempState = TrackingState.AUTO

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseTrakingModeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackPressedCallback()
        initScreen()
    }

    private fun initScreen() {
        binding.chooseTrackingBack.setOnClickListener {
            onBackPressed()
        }

        binding.chooseTrackingModeParentAuto.setOnClickListener {
            setTempState(TrackingState.AUTO)
        }
        binding.chooseTrackingModeParentDemand.setOnClickListener {
            setTempState(TrackingState.DEMAND)
        }
        binding.chooseTrackingModeParentDisable.setOnClickListener {
            setTempState(TrackingState.DISABLE)
        }

        binding.chooseTrackingModeSave.setOnClickListener {
            saveTrackingMode()
        }

        showSaveBtn(false)
        getTrackingState()
    }

    private fun getTrackingState() {

        viewModel.getTrackingState().observe(viewLifecycleOwner) { result ->
            result.onSuccess { trackingState ->
                showState(trackingState)
            }
        }
    }

    private fun showState(state: TrackingState) {

        tempState = state

        binding.chooseTrackingModeParentAutoSelectMark.background = null
        binding.chooseTrackingModeParentDemandSelectMark.background = null
        binding.chooseTrackingModeParentDisableSelectMark.background = null

        setDisableStateCheckView(binding.chooseTrackingModeParentAutoSelectBtn)
        setDisableStateCheckView(binding.chooseTrackingModeParentDemandSelectBtn)
        setDisableStateCheckView(binding.chooseTrackingModeParentDisableSelectBtn)

        when (state) {
            TrackingState.AUTO -> {
                setEnableStateCheckView(binding.chooseTrackingModeParentAutoSelectBtn)
                binding.chooseTrackingModeParentAutoSelectMark.setBackgroundResource(R.color.colorDefButton)
                val modeText = getString(R.string.automatic_tracking)
                binding.chooseTrackingModeSave.text =
                    getString(R.string.proceed_with_mode, modeText)
            }

            TrackingState.DEMAND -> {
                setEnableStateCheckView(binding.chooseTrackingModeParentDemandSelectBtn)
                binding.chooseTrackingModeParentDemandSelectMark.setBackgroundResource(R.color.colorDefButton)
                val modeText = getString(R.string.on_demand_tracking)
                binding.chooseTrackingModeSave.text =
                    getString(R.string.proceed_with_mode, modeText)
            }

            TrackingState.DISABLE -> {
                setEnableStateCheckView(binding.chooseTrackingModeParentDisableSelectBtn)
                binding.chooseTrackingModeParentDisableSelectMark.setBackgroundResource(R.color.colorDefButton)
                val modeText = getString(R.string.tracking_disabled)
                binding.chooseTrackingModeSave.text =
                    getString(R.string.proceed_with_mode, modeText)
            }
        }
    }

    private fun setEnableStateCheckView(imageView: ImageView) {

        imageView.setImageResource(R.drawable.ic_check)
        imageView.setBackgroundResource(R.drawable.ic_trip_circle_blue)
        imageView.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorDefButton))
    }

    private fun setDisableStateCheckView(imageView: ImageView) {

        imageView.setImageBitmap(null)
        imageView.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.circle_setting_tracking_mode_disable
        )
        imageView.backgroundTintList = null
    }

    private fun showSaveBtn(show: Boolean) {
        binding.chooseTrackingModeSave.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    private fun showDialog(tempState: TrackingState) {

        val text = when (tempState) {
            TrackingState.AUTO -> getString(R.string.automatic_tracking)
            TrackingState.DISABLE -> getString(R.string.tracking_disabled)
            TrackingState.DEMAND -> getString(R.string.on_demand_tracking)
        }

        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setTitle(getString(R.string.on_demand_switch, text))
        alertDialog.setMessage(getString(R.string.on_demand_are_you_sure))
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dialog_yes)) { _, _ ->
            viewModel.setOnDemandCurrentJobToCompleted(tempState).observe(viewLifecycleOwner) {
                navBack()
            }
        }
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            getString(R.string.dialog_cancel)
        ) { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    private fun setTempState(trackingState: TrackingState) {

        if (trackingState != tempState) {
            showSaveBtn(true)
        }
        showState(trackingState)
    }

    private fun saveTrackingMode() {

        if (tempState == TrackingState.DEMAND) {
            viewModel.saveTrackingState(tempState)
            navBack()
            return
        }

        viewModel.getOnDemandJobList().observe(viewLifecycleOwner) { result ->
            result.onSuccess { jobList ->
                jobList.find {
                    it.state == OnDemandJobState.CURRENT.name ||
                            it.state == OnDemandJobState.PAUSED.name ||
                            it.state == OnDemandJobState.ACCEPTED.name
                }?.let {
                    showDialog(tempState)
                } ?: run {
                    viewModel.saveTrackingState(tempState)
                    navBack()
                }
            }
        }
    }

    private fun navBack() {
        onBackPressed()
    }
}