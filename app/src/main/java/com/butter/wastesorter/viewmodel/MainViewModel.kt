package com.butter.wastesorter.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.butter.wastesorter.data.Trash
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {

    private val trash: MutableLiveData<ArrayList<Trash>> = MutableLiveData()

    private val imageBitmap: MutableLiveData<Bitmap> = MutableLiveData()

    fun init() {
        val list: ArrayList<Trash> = ArrayList()
        trash.value = list
    }

    fun setTrash(trash: ArrayList<Trash>) {
        this.trash.value = trash
    }

    fun getTrash(): ArrayList<Trash>? = trash.value

    fun setImageBitmap(imageBitmap: Bitmap) {
        this.imageBitmap.value = imageBitmap
    }

    fun getImageBitmap(): Bitmap? = imageBitmap.value

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
}