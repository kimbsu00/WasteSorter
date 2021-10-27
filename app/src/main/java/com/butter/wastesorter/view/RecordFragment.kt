package com.butter.wastesorter.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.butter.wastesorter.R
import com.butter.wastesorter.databinding.FragmentRecordBinding
import com.butter.wastesorter.viewmodel.MainViewModel

class RecordFragment : Fragment() {

    val mainViewModel: MainViewModel by activityViewModels()

    lateinit var binding: FragmentRecordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordBinding.inflate(layoutInflater)

        init()

        return binding.root
    }

    private fun init() {
        
    }

}