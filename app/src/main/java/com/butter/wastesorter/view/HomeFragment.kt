package com.butter.wastesorter.view

import android.app.Activity
import android.content.Context
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
import com.butter.wastesorter.databinding.FragmentHomeBinding
import com.butter.wastesorter.viewmodel.MainViewModel
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class HomeFragment : Fragment() {

    val mainViewModel: MainViewModel by activityViewModels()

    lateinit var binding: FragmentHomeBinding

    lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    lateinit var popupMenuLauncher: ActivityResultLauncher<Intent>

    var moduleEncoder: Module? = null

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

                    mainViewModel.imageBitmap.value = imageBitmap
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

                        mainViewModel.imageBitmap.value = imageBitmap
//                        mainViewModel.uploadImage()

                        Log.i("recognize", recognize(imageBitmap).toString())
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

    private fun recognize(imageBitmap: Bitmap): Int {
        if (moduleEncoder == null) {
            val moduleFileAbsoluteFilePath: String =
                File(assetFilePath(requireContext(), "tmp.ptl")).absolutePath
            Log.i("recognize", moduleFileAbsoluteFilePath)
            moduleEncoder = LiteModuleLoader.load(moduleFileAbsoluteFilePath)
        }

        // preparing input tensor
        val inputTensor: Tensor = TensorImageUtils.bitmapToFloat32Tensor(
            imageBitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )

        // running the model
        val outputTensor: Tensor = moduleEncoder!!.forward(IValue.from(inputTensor)).toTensor()

        // getting tensor content as FloatArray
        val scores: FloatArray = outputTensor.dataAsFloatArray

        // searching for the index with maximum score
        var maxScore: Float = Float.MIN_VALUE
        var maxScoreIdx: Int = -1
        for (i in scores.indices) {
            if (scores[i] > maxScore) {
                maxScore = scores[i]
                maxScoreIdx = i
            }
        }

        return maxScoreIdx
    }

    private fun assetFilePath(context: Context, assetName: String): String {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }

        val inputStream: InputStream = context.assets.open(assetName)
        val os: OutputStream = FileOutputStream(file)

        var buffer: ByteArray = ByteArray(4 * 1024)
        var read: Int = -1
        while (inputStream.read(buffer).also { read = it } != -1) {
            os.write(buffer, 0, read)
        }
        return file.absolutePath
    }

}