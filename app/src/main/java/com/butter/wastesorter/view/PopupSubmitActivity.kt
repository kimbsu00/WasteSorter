package com.butter.wastesorter.view

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.butter.wastesorter.databinding.ActivityPopupSubmitBinding

class PopupSubmitActivity : Activity() {

    lateinit var binding: ActivityPopupSubmitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPopupSubmitBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        init()
    }

    private fun init() {
        binding.apply {
            submitBtn.setOnClickListener {
                setResult(RESULT_OK)
                finish()
            }
            cancelBtn.setOnClickListener {
                setResult(RESULT_CANCELED)
                finish()
            }
        }
    }
}