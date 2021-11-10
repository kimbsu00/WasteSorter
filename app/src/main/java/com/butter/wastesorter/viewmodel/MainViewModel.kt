package com.butter.wastesorter.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.butter.wastesorter.R
import com.butter.wastesorter.data.Trash
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {

    val trash: MutableLiveData<ArrayList<Trash>> = MutableLiveData()

    val selectedTrash: MutableLiveData<Int> = MutableLiveData()

    val imageBitmap: MutableLiveData<Bitmap> = MutableLiveData()

    fun init(context: Context) {
        trash.value = getInitTrashList(context)
        selectedTrash.value = Trash.PLASTIC
    }

    fun uploadImage(): Boolean {
        if (imageBitmap.value != null) {
            val bitmap: Bitmap = imageBitmap.value!!
            val baos: ByteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val storage = Firebase.storage
            val storageRef: StorageReference = storage.reference
            val tmpRef = storageRef.child("unrecognized/tmp.jpg")

            val uploadTask = tmpRef.putBytes(data)
            uploadTask.addOnFailureListener {
                Log.i("uploadImage", "FAIL")
            }.addOnSuccessListener {
                Log.i("uploadImage", "SUCCESS")
            }
        }

        return true
    }

    fun getInitTrashList(context: Context): ArrayList<Trash> {
        val list: ArrayList<Trash> = ArrayList()

        context.apply {
            val plastic: Trash = Trash(
                "플라스틱",
                Trash.PLASTIC,
                arrayListOf(
                    getString(R.string.info_plastic_way_1),
                    getString(R.string.info_plastic_way_2)
                ),
                arrayListOf(
                    getString(R.string.info_plastic_tip_1),
                    getString(R.string.info_plastic_tip_2),
                    getString(R.string.info_plastic_tip_3)
                )
            )
            list.add(plastic)

            val paper: Trash = Trash(
                "종이",
                Trash.PAPER,
                arrayListOf(
                    getString(R.string.info_paper_way_1),
                    getString(R.string.info_paper_way_2),
                    getString(R.string.info_paper_way_3),
                    getString(R.string.info_paper_way_4),
                    getString(R.string.info_paper_way_5)
                ),
                arrayListOf(
                    getString(R.string.info_paper_tip_1),
                    getString(R.string.info_paper_tip_2)
                )
            )
            list.add(paper)

            val cardboard: Trash = Trash(
                "상자",
                Trash.CARDBOARD,
                arrayListOf(
                    getString(R.string.info_cardboard_way_1),
                    getString(R.string.info_cardboard_way_2),
                    getString(R.string.info_cardboard_way_3),
                    getString(R.string.info_cardboard_way_4)
                ),
                arrayListOf(
                    getString(R.string.info_cardboard_tip_1),
                    getString(R.string.info_cardboard_tip_2)
                )
            )
            list.add(cardboard)

            val can: Trash = Trash(
                "캔",
                Trash.CAN,
                arrayListOf(
                    getString(R.string.info_can_way_1),
                    getString(R.string.info_can_way_2),
                    getString(R.string.info_can_way_3),
                    getString(R.string.info_can_way_4),
                    getString(R.string.info_can_way_5)
                ),
                arrayListOf(
                    getString(R.string.info_can_tip_1)
                )
            )
            list.add(can)

            val glass: Trash = Trash(
                "유리",
                Trash.GLASS,
                arrayListOf(
                    getString(R.string.info_glass_way_1),
                    getString(R.string.info_glass_way_2),
                    getString(R.string.info_glass_way_3),
                    getString(R.string.info_glass_way_4),
                    getString(R.string.info_glass_way_5),
                    getString(R.string.info_glass_way_6)
                ),
                arrayListOf(
                    getString(R.string.info_glass_tip_1),
                    getString(R.string.info_glass_tip_2),
                    getString(R.string.info_glass_tip_3)
                )
            )
            list.add(glass)

            val metal: Trash = Trash(
                "철",
                Trash.METAL,
                arrayListOf(
                    getString(R.string.info_metal_way_1),
                    getString(R.string.info_metal_way_2)
                ),
                arrayListOf(
                    getString(R.string.info_metal_tip_1)
                )
            )
            list.add(metal)
        }

        return list
    }
}