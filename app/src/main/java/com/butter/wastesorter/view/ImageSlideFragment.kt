package com.butter.wastesorter.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.butter.wastesorter.R
import com.butter.wastesorter.databinding.FragmentImageSlideBinding

class ImageSlideFragment(val image: Int) : Fragment() {

    lateinit var binding: FragmentImageSlideBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imgSlideImage.setImageResource(image)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageSlideBinding.inflate(layoutInflater)
        return binding.root
    }

}