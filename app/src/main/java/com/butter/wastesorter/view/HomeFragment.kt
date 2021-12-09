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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.butter.wastesorter.data.Trash
import com.butter.wastesorter.databinding.FragmentHomeBinding
import com.butter.wastesorter.ml.ConvertedModel
import com.butter.wastesorter.viewmodel.MainViewModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    val mainViewModel: MainViewModel by activityViewModels()

    lateinit var binding: FragmentHomeBinding

    lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    lateinit var popupMenuLauncher: ActivityResultLauncher<Intent>

    var listener: OnFragmentInteraction? = null

    interface OnFragmentInteraction {
        fun showImageFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val imageBitmap = it.data?.extras?.get("data") as Bitmap

                    mainViewModel.imageBitmap.value = imageBitmap
                    listener?.showImageFragment()
                }
            }
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val imageUri: Uri? = it.data?.data
                    if (imageUri != null) {
                        val imageBitmap = when {
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

                        mainViewModel.imageBitmap.value = imageBitmap
                        listener?.showImageFragment()
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

            textView3.setOnClickListener {
                Intent(requireContext(), CameraActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }

}