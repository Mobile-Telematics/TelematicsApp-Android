import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.telematics.content.utils.BaseFragment
import com.telematics.obd.databinding.FragmentObdFeatureHostBinding


class OBDFeatureHost : BaseFragment() {

    private lateinit var binding: FragmentObdFeatureHostBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentObdFeatureHostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.obdBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.obdExit.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}