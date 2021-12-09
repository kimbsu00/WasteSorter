package com.butter.wastesorter.view

import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.scale
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.butter.wastesorter.R
import com.butter.wastesorter.data.Rect
import com.butter.wastesorter.data.Trash
import com.butter.wastesorter.databinding.FragmentImageBinding
import com.butter.wastesorter.ml.ConvertedModel
import com.butter.wastesorter.ml.Model
import com.butter.wastesorter.viewmodel.MainViewModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.sqrt

class ImageFragment : Fragment() {

    val mainViewModel: MainViewModel by activityViewModels()

    lateinit var binding: FragmentImageBinding

    val p: Paint = Paint()
    val modelThreadHandler: ModelThreadHandler = ModelThreadHandler()
    val myProgressBar: MyProgressBar = MyProgressBar()

    var listener: OnFragmentInteraction? = null

    interface OnFragmentInteraction {
        fun showInfoFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageBinding.inflate(layoutInflater)

        init()

        return binding.root
    }

    private fun init() {
        p.setColor(Color.parseColor("#43aa8b"))
        p.strokeWidth = 5f

        mainViewModel.imageBitmap.observe(viewLifecycleOwner, Observer { imageBitmap: Bitmap? ->
            if (imageBitmap == null) {
                binding.apply {
                    noImageLayout.visibility = View.VISIBLE
                    yesImageLayout.visibility = View.GONE
                }
            } else {
                binding.apply {
                    noImageLayout.visibility = View.GONE
                    yesImageLayout.visibility = View.VISIBLE
                    selectedImageLayout.visibility = View.GONE
                }

                myProgressBar.progressON(requireActivity(), "Loading..")
                val thread: ModelThread = ModelThread(1)
                thread.start()
            }
        })

        binding.apply {
            borderImageView.setOnClickListener {
                selectedImageLayout.visibility = View.VISIBLE
            }

            borderImageView.setOnTouchListener { view, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_UP -> {
                        makeCutImageBitmap(view, motionEvent)
                    }
                    else -> {
                        false
                    }
                }
            }

            confirmBtn.setOnClickListener {
                myProgressBar.progressON(requireActivity(), "Loading..")
                val thread: ModelThread = ModelThread(2)
                thread.start()
            }

            cancelBtn.setOnClickListener {
                selectedImageLayout.visibility = View.GONE
            }
        }
    }

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

    private fun model(bitmap: Bitmap): ArrayList<Rect> {
        val imageProcessor: ImageProcessor =
            ImageProcessor.Builder()
                .add(ResizeOp(320, 320, ResizeOp.ResizeMethod.BILINEAR))
                .add(NormalizeOp(0.0f, 255.0f)).build()

        val tensorImage: TensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)
        val processedImage: TensorImage = imageProcessor.process(tensorImage)

        val model = Model.newInstance(requireContext())

        // Creates inputs for reference.
        val inputFeature0 =
            TensorBuffer.createFixedSize(intArrayOf(1, 320, 320, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(processedImage.buffer)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0: TensorBuffer = outputs.outputFeature0AsTensorBuffer
        val outputFeature1: TensorBuffer = outputs.outputFeature1AsTensorBuffer
        val outputFeature2: TensorBuffer = outputs.outputFeature2AsTensorBuffer
        val outputFeature3: TensorBuffer = outputs.outputFeature3AsTensorBuffer

        val results0: FloatArray = outputFeature0.floatArray    // [ymin, xmin, ymax, xmax]
        val results1: FloatArray = outputFeature1.floatArray
        val results2: FloatArray = outputFeature2.floatArray    // score
        val results3: FloatArray = outputFeature3.floatArray

        val ret: ArrayList<Rect> = ArrayList()
        for (i in 0..results2.size - 1) {
            val ymin: Float = if (results0[i * 4] < 0) 0f else results0[i * 4]
            val xmin: Float = if (results0[i * 4 + 1] < 0) 0f else results0[i * 4 + 1]
            val ymax: Float = if (results0[i * 4 + 2] < 0) 0f else results0[i * 4 + 2]
            val xmax: Float = if (results0[i * 4 + 3] < 0) 0f else results0[i * 4 + 3]

            if (results2[i] > 0.3) {
                ret.add(Rect(xmin, ymin, xmax, ymax))
            }
        }

        // Releases model resources if no longer used.
        model.close()

        return ret
    }

    private fun makeCutImageBitmap(view: View, motionEvent: MotionEvent): Boolean {
        val width: Int = view.width
        val height: Int = view.height
        val x: Float = view.x
        val y: Float = view.y
        val cX: Float = motionEvent.x
        val cY: Float = motionEvent.y

        val normalizeX: Float = (cX - x) / width.toFloat()
        val normalizeY: Float = (cY - y) / height.toFloat()

        val rect: Rect? = checkRectIn(normalizeX, normalizeY)
        return when (rect) {
            null -> {
                Log.i("ImageFragment", "rect is null")
                true
            }
            else -> {
                Log.i("ImageFragment", "rect is not null")
                val scaledBitmap: Bitmap =
                    Bitmap.createScaledBitmap(
                        mainViewModel.imageBitmap.value!!,
                        320,
                        320,
                        true
                    )
                        .copy(Bitmap.Config.ARGB_8888, true)
                Log.i("cutImageBitmap", "${scaledBitmap.width}")
                Log.i("cutImageBitmap", "${((rect.x2 - rect.x1) * 320).toInt()}")

                val cutImageBitmap: Bitmap = Bitmap.createBitmap(
                    scaledBitmap,
                    (rect.x1 * 320).toInt(),
                    (rect.y1 * 320).toInt(),
                    ((rect.x2 - rect.x1) * 320).toInt(),
                    ((rect.y2 - rect.y1) * 320).toInt(),
                )
                mainViewModel.cutImageBitmap.value = cutImageBitmap
                binding.selectedImageLayout.visibility = View.VISIBLE
                binding.selectedImageView.setImageBitmap(
                    cutImageBitmap.scale(
                        cutImageBitmap.width * 3,
                        cutImageBitmap.height * 3,
                        true
                    )
                )
                false
            }
        }
    }

    private fun checkRectIn(normalizeX: Float, normalizeY: Float): Rect? {
        var ret: Rect? = null
        var minDist: Float = Float.MAX_VALUE

        val posList: ArrayList<Rect> = mainViewModel.posList.value!!
        for (rect in posList) {
            rect.apply {
                if (normalizeX in x1..x2 && normalizeY in y1..y2) {
                    var dist: Float =
                        (x1 - normalizeX) * (x1 - normalizeX) + (y1 - normalizeY) * (y1 - normalizeY)
                    dist = sqrt(dist)

                    if (dist < minDist) {
                        ret = rect
                        minDist = dist
                    }
                }
            }
        }

        // 객체 인식 모델이 아무런 객체를 인식하지 못한 경우,
        // 이미지 전체를 하나의 객체로 인식한 것 처럼 설정함
        if (posList.isEmpty()) {
            ret = Rect(0f, 0f, 1f, 1f)
        }

        return ret
    }

    inner class ModelThreadHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            val bundle: Bundle = msg.data
            if (!bundle.isEmpty) {
                val type: Int = bundle.getInt("type")
                when (type) {
                    // ssd_mobilenet_v2_mnasfpn_coco 모델 사용 후처리
                    1 -> {
                        val success: Boolean = bundle.getBoolean("isSuccess")
                        if (success) {
                            val posList: ArrayList<Rect> =
                                bundle.getSerializable("posList") as ArrayList<Rect>
                            mainViewModel.posList.value = posList

                            val borderBitmap: Bitmap =
                                bundle.getParcelable<Bitmap>("borderBitmap") as Bitmap
                            binding.borderImageView.setImageBitmap(borderBitmap)
                        } else {
                            Log.e("ModelThreadHandler", "success is false when type is $type")
                        }
                    }
                    // Inception V3 모델 사용 후처리
                    2 -> {
                        val success: Boolean = bundle.getBoolean("isSuccess")
                        if (success) {
                            val code: Int = bundle.getInt("code")
                            val time: String = bundle.getString("time") as String
                            mainViewModel.selectedTrash.value = code
                            mainViewModel.addRecord(code, time)
                            listener?.showInfoFragment()
                        } else {
                            Log.e("ModelThreadHandler", "success is false when type is $type")
                        }
                    }
                }
                myProgressBar.progressOFF()
            }
        }
    }

    inner class ModelThread(val type: Int) : Thread() {
        override fun run() {
            val message = modelThreadHandler.obtainMessage()
            val bundle: Bundle = Bundle()
            bundle.putInt("type", type)

            when (type) {
                // ssd_mobilenet_v2_mnasfpn_coco 모델 사용
                1 -> {
                    val imageBitmap: Bitmap? = mainViewModel.imageBitmap.value
                    if (imageBitmap != null) {
                        // ssd_mobilenet_v2_mnasfpn_coco 모델 돌리고, Border Box 추가한 이미지로 변경하면 된다.
                        val posList: ArrayList<Rect> = model(imageBitmap)

                        val borderBitmap: Bitmap =
                            Bitmap.createScaledBitmap(imageBitmap, 320, 320, true)
                                .copy(Bitmap.Config.ARGB_8888, true)
                        val canvas: Canvas = Canvas(borderBitmap)
                        val height: Int = borderBitmap.height
                        val width: Int = borderBitmap.width

                        for (rect in posList) {
                            rect.apply {
                                val path: Path = Path()
                                path.moveTo(x1 * width, y1 * height)    // Left Top
                                path.lineTo(x1 * width, y2 * height)    // Left Bottom
                                path.lineTo(x2 * width, y2 * height)    // Right Bottom
                                path.lineTo(x2 * width, y1 * height)    // Left Top
                                path.lineTo(x1 * width, y1 * height)

                                path.moveTo(x1 * width + 5, y1 * height + 5)
                                path.lineTo(x2 * width - 5, y1 * height + 5)
                                path.lineTo(x2 * width - 5, y2 * height - 5)
                                path.lineTo(x1 * width + 5, y2 * height - 5)
                                path.lineTo(x1 * width + 5, y1 * height + 5)
                                canvas.drawPath(path, p)
                            }
                        }
                        bundle.putBoolean("isSuccess", true)
                        bundle.putParcelable("borderBitmap", borderBitmap)
                        bundle.putSerializable("posList", posList)
                    } else {
                        bundle.putBoolean("isSuccess", false)
                    }
                }
                // Inception V3 모델 사용
                2 -> {
                    if (mainViewModel.cutImageBitmap.value != null) {
                        val code: Int = recognize(mainViewModel.cutImageBitmap.value!!)
                        val now: Long = System.currentTimeMillis()
                        val date: Date = Date(now)
                        val sdf: SimpleDateFormat = SimpleDateFormat("yyyy년 MM월 dd일 hh시 mm분 ss초")
                        val time: String = sdf.format(date)

                        mainViewModel.myDBHelper.value!!.insertRecord(code, time)
                        bundle.putBoolean("isSuccess", true)
                        bundle.putInt("code", code)
                        bundle.putString("time", time)
                    } else {
                        Log.e("ModelThread", "cutImageBitmap is null when type = $type")
                        bundle.putBoolean("isSuccess", false)
                    }
                }
            }

            message.data = bundle
            modelThreadHandler.sendMessage(message)
        }
    }
}