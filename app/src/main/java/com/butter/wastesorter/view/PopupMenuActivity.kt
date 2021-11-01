package com.butter.wastesorter.view

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import com.butter.wastesorter.R
import com.butter.wastesorter.databinding.ActivityPopupMenuBinding

class PopupMenuActivity : Activity() {

    lateinit var binding: ActivityPopupMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPopupMenuBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(binding.root)

        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        init()
    }

    override fun onBackPressed() {
        val retIntent: Intent = Intent()
        retIntent.putExtra("type", 0)
        setResult(RESULT_OK, retIntent)
        finish()
    }

    private fun init() {
        binding.apply {
            cameraBtn.setOnClickListener {
                val retIntent: Intent = Intent()
                retIntent.putExtra("type", 1)
                setResult(RESULT_OK, retIntent)
                finish()
            }
            galleryBtn.setOnClickListener {
                val retIntent: Intent = Intent()
                retIntent.putExtra("type", 2)
                setResult(RESULT_OK, retIntent)
                finish()
            }
        }
    }
}