package ui.step2_wizard.crop

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.isseiaoki.simplecropview.CropImageView
import com.telematics.content.utils.BaseFragment
import com.telematics.data.utils.PhotoUtils
import com.telematics.obd.R
import com.telematics.obd.databinding.FragmentCropImageBinding
import dagger.hilt.android.AndroidEntryPoint
import ui.CommonObdViewModel
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class CropFragment : BaseFragment() {

    companion object {
        const val CROP_INPUT_FILE_KEY = "input"
        const val CROP_RESULT_FILE_KEY = "result"
    }

    @Inject
    lateinit var viewModel: CropViewModel
    private lateinit var commonObdViewModel: CommonObdViewModel


    private lateinit var binding: FragmentCropImageBinding

    private var imageFile: String = ""
    private var resultFile: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCropImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commonObdViewModel =
            ViewModelProvider(requireActivity()).get(CommonObdViewModel::class.java)


        imageFile = arguments?.getString(CROP_INPUT_FILE_KEY) ?: ""
        resultFile = arguments?.getString(CROP_RESULT_FILE_KEY) ?: ""

        setListeners()
        init()
    }

    private fun setListeners() {

        setBackPressedCallback()

        binding.cropToolbar.setNavigationOnClickListener { onBackPressed() }
        binding.cropReady.setOnClickListener {
            val bitmap = try {
                binding.cropImageView.croppedBitmap
            } catch (e: Exception) {
                null
            }
            save(bitmap)
        }
        binding.cropRotateLeft.setOnClickListener { rotateLeft() }
        binding.cropRotateRight.setOnClickListener { rotateRight() }
    }

    private fun init() {

        setCropMode(CropImageView.CropMode.SQUARE)

        val file = File(imageFile)
        val isImageExists = file.exists()
        if (!isImageExists) {
            nextStep(null)
            return
        }

        setImageBitmap(BitmapFactory.decodeFile(imageFile))

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imageFile, options)

        val max = options.outWidth.coerceAtLeast(options.outHeight)
        val scale = max / 120f
        val newWidth = (options.outWidth / scale).toInt()
        val newHeight = (options.outHeight / scale).toInt()
        val b = viewModel.decodeSampledBitmap(file.path, newWidth, newHeight)
        setImageBitmap(b)
    }

    private fun setCropMode(mode: CropImageView.CropMode = CropImageView.CropMode.SQUARE) {
        binding.cropImageView.setCropMode(mode)
    }

    private fun setImageBitmap(bitmap: Bitmap) {
        binding.cropImageView.imageBitmap = bitmap
    }

    private fun rotateRight() {
        binding.cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D, 1000)
    }

    private fun rotateLeft() {
        binding.cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D, 1000)
    }

    private fun save(bitmap: Bitmap?) {
        if (bitmap != null) {
            val file = File(resultFile)
            PhotoUtils.saveToFile(bitmap, file, Bitmap.CompressFormat.PNG)
            nextStep(resultFile)
        }
    }

    private fun showFilePathError() {
        showMessage(getString(R.string.something_went_wrong))
    }

    private fun nextStep(filePath: String?) {

        filePath?.let {
            commonObdViewModel.setOdometerImage(it)
            findNavController().navigate(R.id.action_cropPhotoFragment_to_confirmPhotoFragment)
        } ?: run {
            showFilePathError()
        }
    }
}