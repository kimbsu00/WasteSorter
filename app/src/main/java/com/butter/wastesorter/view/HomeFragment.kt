package com.butter.wastesorter.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.BitmapCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.butter.wastesorter.data.ModelClasses
import com.butter.wastesorter.databinding.FragmentHomeBinding
import com.butter.wastesorter.ml.ConvertedModel
import com.butter.wastesorter.viewmodel.MainViewModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer

class HomeFragment : Fragment() {

    val mainViewModel: MainViewModel by activityViewModels()

    lateinit var binding: FragmentHomeBinding

    lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    lateinit var popupMenuLauncher: ActivityResultLauncher<Intent>

    var listener: OnFragmentInteraction? = null

    interface OnFragmentInteraction {
        fun showInfoFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    var imageBitmap = it.data?.extras?.get("data") as Bitmap
                    // resize bitmap image
//                    imageBitmap = resizeBitmap(imageBitmap)
                    mainViewModel.imageBitmap.value = imageBitmap

                    // task for recognize image
                    mainViewModel.selectedTrash.value = recognize(imageBitmap)
                    listener?.showInfoFragment()
                }
            }
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val imageUri: Uri? = it.data?.data
                    if (imageUri != null) {
                        var imageBitmap = when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                                val source = ImageDecoder.createSource(
                                    requireContext().contentResolver,
                                    imageUri
                                )
                                ImageDecoder.decodeBitmap(
                                    source,
                                    ImageDecoder.OnHeaderDecodedListener { decoder, info, source ->
                                        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                                        decoder.isMutableRequired = true
                                    })
                            }
                            else -> MediaStore.Images.Media.getBitmap(
                                requireContext().contentResolver,
                                imageUri
                            )
                        }
                        // resize bitmap image
//                        imageBitmap = resizeBitmap(imageBitmap)
                        mainViewModel.imageBitmap.value = imageBitmap

                        // task for recognize image
                        mainViewModel.selectedTrash.value = recognize(imageBitmap)
                        listener?.showInfoFragment()
                    }
                }
            }
        popupMenuLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val type: Int = it.data!!.getIntExtra("type", 0)
                    when (type) {
                        // Camera
                        1 -> {
                            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                                takePictureIntent.resolveActivity(requireContext().packageManager)
                                    ?.also {
                                        cameraLauncher.launch(takePictureIntent)
                                    }
                            }
                        }
                        // Gallery
                        2 -> {
                            Intent(Intent.ACTION_PICK).also { takePictureIntent ->
                                takePictureIntent.setDataAndType(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    "image/*"
                                )
                                galleryLauncher.launch(takePictureIntent)
                            }
                        }
                    }
                }
            }

        init()

        return binding.root
    }

    private fun init() {
        binding.apply {
            startBtn.setOnClickListener {
                Intent(requireContext(), PopupMenuActivity::class.java).also { popupMenuIntent ->
                    popupMenuLauncher.launch(popupMenuIntent)
                }
            }

            imageView2.setOnClickListener {
                it.visibility = View.GONE
            }
        }
    }

    private fun resizeBitmap(bitmap: Bitmap): Bitmap =
        Bitmap.createScaledBitmap(bitmap, 512, 384, true)

    private fun recognize(bitmap: Bitmap): Int {
        val imageProcessor: ImageProcessor =
            ImageProcessor.Builder().add(ResizeOp(384, 512, ResizeOp.ResizeMethod.BILINEAR)).build()

        val tensorImage: TensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)
        val processedImage: TensorImage = imageProcessor.process(tensorImage)

        val model = ConvertedModel.newInstance(requireContext())

        // Creates inputs for reference.
        val inputFeature0 =
            TensorBuffer.createFixedSize(intArrayOf(1, 512, 384, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(tensorImage.buffer)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0: TensorBuffer = outputs.outputFeature0AsTensorBuffer

        val results: FloatArray = outputFeature0.floatArray
        for (idx in results.indices) {
            Log.i("result[$idx]", "${results[idx]}")
        }

        // Releases model resources if no longer used.
        model.close()

        return 0
    }

}