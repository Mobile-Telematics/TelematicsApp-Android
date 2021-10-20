package ui.step1_vehicles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.telematics.content.utils.BaseFragment
import com.telematics.domain.model.carservice.Vehicle
import com.telematics.obd.R
import com.telematics.obd.databinding.FragmentObdVehiclesBinding
import dagger.hilt.android.AndroidEntryPoint
import ui.CommonObdViewModel
import ui.ObdViewModel
import javax.inject.Inject

@AndroidEntryPoint
class VehiclesFragment : BaseFragment() {

    @Inject
    lateinit var obdViewModel: ObdViewModel
    private lateinit var commonObdViewModel: CommonObdViewModel

    private lateinit var binding: FragmentObdVehiclesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentObdVehiclesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        commonObdViewModel =
            ViewModelProvider(requireActivity()).get(CommonObdViewModel::class.java)

        binding.refresh.setOnRefreshListener {
            observeVehicles()
        }
        binding.addCarButton.setOnClickListener {
            createVehicle()
        }

        observeVehicles()
        observeLastSession()
    }

    private fun observeVehicles() {

        obdViewModel.getVehicles().observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                bindVehicleList(it)
            }
            result.onFailure {
                showEmptyVehicle(true)
            }
        }
    }

    private fun bindVehicleList(list: List<Vehicle>) {

        binding.refresh.isRefreshing = false

        if (list.isEmpty()) {
            showEmptyVehicle(true)
            return
        }

        showEmptyVehicle(false)

        val adapter = VehicleListAdapter(list)
        adapter.setOnClickListener(object : VehicleListAdapter.ClickListeners {
            override fun onItemClick(vehicle: Vehicle, listItemPosition: Int) {
                commonObdViewModel.setVehicle(vehicle)
                nextStep(vehicle)
            }
        })
        binding.vehiclesRV.layoutManager =
            LinearLayoutManager(requireContext())
        binding.vehiclesRV.adapter = adapter
    }

    private fun showEmptyVehicle(show: Boolean) {

        if (show)
            showMessage(R.string.obd_no_vehicles_error)
    }

    private fun observeLastSession() {

        obdViewModel.getLastSession().observe(viewLifecycleOwner) { result ->
            result.onSuccess { lastSessionTime ->

                val timestamp =
                    if (lastSessionTime == 0L) 0L else (System.currentTimeMillis() - lastSessionTime) / 1000 / 60
                binding.connectedStatus.text = when (timestamp) {
                    0L ->
                        getString(R.string.obd_car_last_session_never)
                    in 1L..59L ->
                        String.format(getString(R.string.obd_car_last_session_min), timestamp)
                    in 60L..1440L ->
                        String.format(
                            getString(R.string.obd_car_last_session_hours),
                            timestamp / 60
                        )
                    in 1440..Long.MAX_VALUE -> String.format(
                        getString(R.string.obd_car_last_session_days),
                        timestamp / 60 / 24
                    )
                    else -> getString(R.string.obd_car_last_session_never)
                }
            }
        }
    }

    private fun createVehicle() {

        findNavController().navigate(R.id.action_vehiclesFragment_to_obdAddVehicle)
    }

    private fun nextStep(car: Vehicle) {

        if (!car.activated)
            findNavController().navigate(R.id.action_vehiclesFragment_to_vehicleWizardFragment)
        else
            findNavController().navigate(R.id.action_vehiclesFragment_to_deviceConnectFragment)
    }
}
