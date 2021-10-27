package com.butter.wastesorter.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.butter.wastesorter.R
import com.butter.wastesorter.databinding.FragmentHomeBinding
import com.butter.wastesorter.viewmodel.MainViewModel

class HomeFragment : Fragment() {

    val mainViewModel: MainViewModel by activityViewModels()

    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        init()

        return binding.root
    }

    private fun init() {
        
    }

}