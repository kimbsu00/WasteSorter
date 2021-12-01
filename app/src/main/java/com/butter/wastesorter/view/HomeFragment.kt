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
                    val imageBitmap = it.data?.extras?.get("data") as Bitmap
                    // resize bitmap image
//                    imageBitmap = resizeBitmap(imageBitmap)
                    mainViewModel.imageBitmap.value = imageBitmap

                    // task for recognize image
                    val code: Int = recognize(imageBitmap)
                    taskAfterRecognize(code)
                    listener?.showInfoFragment()
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
                        // resize bitmap image
//                        imageBitmap = resizeBitmap(imageBitmap)
//                        binding.imageView2.visibility = View.VISIBLE
//                        binding.imageView2.setImageBitmap(resizeBitmap(imageBitmap))
//                        binding.imageView2.setImageBitmap(imageBitmap)

                        mainViewModel.imageBitmap.value = imageBitmap

                        // task for recognize image
                        val code: Int = recognize(imageBitmap)
                        taskAfterRecognize(code)
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

            textView3.setOnClickListener {
                Intent(requireContext(), CameraActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }

    private fun resizeBitmap(bitmap: Bitmap): Bitmap =
        Bitmap.createScaledBitmap(bitmap, 512, 384, true)

    private fun recognize(bitmap: Bitmap): Int {
        val imageProcessor: ImageProcessor =
            ImageProcessor.Builder()
                .add(ResizeOp(384, 512, ResizeOp.ResizeMethod.BILINEAR))
                .add(NormalizeOp(0.0f, 255.0f)).build()

        val tensorImage: TensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)
        val processedImage: TensorImage = imageProcessor.process(tensorImage)

        val model = ConvertedModel.newInstance(requireContext())

        // Creates inputs for reference.
        val inputFeature0 =
            TensorBuffer.createFixedSize(intArrayOf(1, 512, 384, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(processedImage.buffer)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0: TensorBuffer = outputs.outputFeature0AsTensorBuffer

        /*
        결과 값에 대한 쓰레기 정보
        0: CardBoard
        1: Glass
        2: Metal
        3: Paper
        4: Plastic
        5: Trash
        */
        val results: FloatArray = outputFeature0.floatArray
        var max: Float = -1.0f
        var maxIdx: Int = -1
        for (idx in results.indices) {
            Log.i("result[$idx]", "${results[idx]}")
            if (max < results[idx]) {
                max = results[idx]
                maxIdx = idx
            }
        }

        // Releases model resources if no longer used.
        model.close()

        return when (maxIdx) {
            0 -> Trash.CARDBOARD
            1 -> Trash.GLASS
            2 -> Trash.METAL
            3 -> Trash.PAPER
            4 -> Trash.PLASTIC
            5 -> Trash.TRASH
            else -> Trash.TRASH
        }
    }

    fun taskAfterRecognize(code: Int) {
        val now: Long = System.currentTimeMillis()
        val date: Date = Date(now)
        val sdf: SimpleDateFormat = SimpleDateFormat("yyyy년 MM월 dd일 hh시 mm분 ss초")
        val time: String = sdf.format(date)

        mainViewModel.selectedTrash.value = code
        mainViewModel.addRecord(code, time)

        val thread: Thread = object : Thread() {
            override fun run() {
                mainViewModel.myDBHelper.value!!.insertRecord(code, time)
            }
        }
        thread.start()
    }

}