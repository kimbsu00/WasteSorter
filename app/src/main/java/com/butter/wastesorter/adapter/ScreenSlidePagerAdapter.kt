package com.butter.wastesorter.adapter

import android.media.Image
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.butter.wastesorter.R
import com.butter.wastesorter.data.Trash
import com.butter.wastesorter.view.ImageSlideFragment

class ScreenSlidePagerAdapter(fa: FragmentActivity, var pageNum: Int, var type: Int) :
    FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = pageNum

    override fun createFragment(position: Int): Fragment {
        Log.i("ScreenSlidePagerAdapter", "position is $position")
        Log.i("ScreenSlidePagerAdapter", "type is $type")
        return when (position + 10 * type) {
            // Plastic
            0 -> ImageSlideFragment(R.drawable.plastic_1)
            1 -> ImageSlideFragment(R.drawable.plastic_2)
            2 -> ImageSlideFragment(R.drawable.plastic_3)
            3 -> ImageSlideFragment(R.drawable.plastic_4)
            4 -> ImageSlideFragment(R.drawable.plastic_5)
            5 -> ImageSlideFragment(R.drawable.plastic_6)
            // Paper
            10 -> ImageSlideFragment(R.drawable.paper_1)
            // Cardboard
            20 -> ImageSlideFragment(R.drawable.paper_1)
            // Can
            30 -> ImageSlideFragment(R.drawable.can_1)
            31 -> ImageSlideFragment(R.drawable.can_2)
            32 -> ImageSlideFragment(R.drawable.can_3)
            // Glass
            40 -> ImageSlideFragment(R.drawable.glass_1)
            // Metal
            50 -> ImageSlideFragment(R.drawable.metal_1)
            51 -> ImageSlideFragment(R.drawable.metal_2)
            // else
            else -> ImageSlideFragment(R.drawable.plastic_1)
        }
    }
}