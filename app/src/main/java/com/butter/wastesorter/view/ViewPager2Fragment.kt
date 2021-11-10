package com.butter.wastesorter.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.butter.wastesorter.R
import com.butter.wastesorter.adapter.ScreenSlidePagerAdapter
import com.butter.wastesorter.data.Trash
import com.butter.wastesorter.databinding.FragmentViewPager2Binding

class ViewPager2Fragment(val trashCode: Int) : Fragment() {

    lateinit var binding: FragmentViewPager2Binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            val pageNum: Int = when (trashCode) {
                Trash.PLASTIC -> 6
                Trash.PAPER -> 1
                Trash.CARDBOARD -> 1
                Trash.CAN -> 3
                Trash.GLASS -> 1
                Trash.METAL -> 2
                else -> 1
            }
            viewPager.adapter = ScreenSlidePagerAdapter(requireActivity(), pageNum, trashCode)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewPager2Binding.inflate(layoutInflater)
        return binding.root
    }

}