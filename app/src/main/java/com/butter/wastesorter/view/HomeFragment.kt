package com.butter.wastesorter.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.butter.wastesorter.databinding.FragmentHomeBinding
import com.butter.wastesorter.viewmodel.MainViewModel

class HomeFragment : Fragment() {

    val mainViewModel: MainViewModel by activityViewModels()

    lateinit var binding: FragmentHomeBinding

    lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    lateinit var popupMenuLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val imageBitmap = it.data?.extras?.get("data") as Bitmap
                    binding.imageView2.setImageBitmap(imageBitmap)
                    binding.imageView2.visibility = View.VISIBLE

                    mainViewModel.setImageBitmap(imageBitmap)
                    mainViewModel.uploadImage()
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
                                ImageDecoder.decodeBitmap(source)
                            }
                            else -> MediaStore.Images.Media.getBitmap(
                                requireContext().contentResolver,
                                imageUri
                            )
                        }
                        binding.imageView2.setImageBitmap(imageBitmap)
                        binding.imageView2.visibility = View.VISIBLE

                        mainViewModel.setImageBitmap(imageBitmap)
                        mainViewModel.uploadImage()
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

}