package ui.step_unknown_error

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.telematics.content.utils.BaseFragment
import com.telematics.obd.databinding.FragmentOdbOopsBinding

class CouldNotConnectFragment : BaseFragment() {

    private lateinit var binding: FragmentOdbOopsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOdbOopsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tryAgainButton.setOnClickListener {
            onBackPressed()
        }
    }
}
