package com.butter.wastesorter.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.butter.wastesorter.R
import com.butter.wastesorter.adapter.InfoFragmentAdapter
import com.butter.wastesorter.adapter.ScreenSlidePagerAdapter
import com.butter.wastesorter.data.Trash
import com.butter.wastesorter.databinding.FragmentInfoBinding
import com.butter.wastesorter.viewmodel.MainViewModel

class InfoFragment : Fragment() {

    val mainViewModel: MainViewModel by activityViewModels()

    lateinit var binding: FragmentInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInfoBinding.inflate(layoutInflater)

        init()

        return binding.root
    }

    private fun init() {
        mainViewModel.selectedTrash.observe(viewLifecycleOwner, Observer { trashCode ->
            when (trashCode) {
                Trash.PLASTIC -> {
                    binding.apply {
                        replaceFragment(ViewPager2Fragment(Trash.PLASTIC))

                        wayRecyclerView.adapter =
                            InfoFragmentAdapter(mainViewModel.trash.value!![Trash.PLASTIC].ways)
                        tipRecyclerView.adapter =
                            InfoFragmentAdapter(mainViewModel.trash.value!![Trash.PLASTIC].tips)
                        trashName.text = getString(R.string.info_plastic_name)
                        basicInfo1.text = getString(R.string.info_plastic_1)
                        basicInfo2.text = getString(R.string.info_plastic_2)
                        basicInfo3.text = getString(R.string.info_plastic_3)
                    }
                }
                Trash.PAPER -> {
                    binding.apply {
                        replaceFragment(ViewPager2Fragment(Trash.PAPER))

                        wayRecyclerView.adapter =
                            InfoFragmentAdapter(mainViewModel.trash.value!![Trash.PAPER].ways)
                        tipRecyclerView.adapter =
                            InfoFragmentAdapter(mainViewModel.trash.value!![Trash.PAPER].tips)
                        trashName.text = getString(R.string.info_paper_name)
                        basicInfo1.text = getString(R.string.info_paper_1)
                        basicInfo2.text = getString(R.string.info_paper_2)
                        basicInfo3.text = getString(R.string.info_paper_3)
                    }
                }
                Trash.CARDBOARD -> {
                    binding.apply {
                        replaceFragment(ViewPager2Fragment(Trash.CARDBOARD))

                        wayRecyclerView.adapter =
                            InfoFragmentAdapter(mainViewModel.trash.value!![Trash.CARDBOARD].ways)
                        tipRecyclerView.adapter =
                            InfoFragmentAdapter(mainViewModel.trash.value!![Trash.CARDBOARD].tips)
                        trashName.text = getString(R.string.info_cardboard_name)
                        basicInfo1.text = getString(R.string.info_cardboard_1)
                        basicInfo2.text = getString(R.string.info_cardboard_2)
                        basicInfo3.text = getString(R.string.info_cardboard_3)
                    }
                }
                Trash.CAN -> {
                    binding.apply {
                        replaceFragment(ViewPager2Fragment(Trash.CAN))

                        wayRecyclerView.adapter =
                            InfoFragmentAdapter(mainViewModel.trash.value!![Trash.CAN].ways)
                        tipRecyclerView.adapter =
                            InfoFragmentAdapter(mainViewModel.trash.value!![Trash.CAN].tips)
                        trashName.text = getString(R.string.info_can_name)
                        basicInfo1.text = getString(R.string.info_can_1)
                        basicInfo2.text = getString(R.string.info_can_2)
                        basicInfo3.text = getString(R.string.info_can_3)
                    }
                }
                Trash.GLASS -> {
                    binding.apply {
                        replaceFragment(ViewPager2Fragment(Trash.GLASS))

                        wayRecyclerView.adapter =
                            InfoFragmentAdapter(mainViewModel.trash.value!![Trash.GLASS].ways)
                        tipRecyclerView.adapter =
                            InfoFragmentAdapter(mainViewModel.trash.value!![Trash.GLASS].tips)
                        trashName.text = getString(R.string.info_glass_name)
                        basicInfo1.text = getString(R.string.info_glass_1)
                        basicInfo2.text = getString(R.string.info_glass_2)
                        basicInfo3.text = getString(R.string.info_glass_3)
                    }
                }
                Trash.METAL -> {
                    binding.apply {
                        replaceFragment(ViewPager2Fragment(Trash.METAL))

                        wayRecyclerView.adapter =
                            InfoFragmentAdapter(mainViewModel.trash.value!![Trash.METAL].ways)
                        tipRecyclerView.adapter =
                            InfoFragmentAdapter(mainViewModel.trash.value!![Trash.METAL].tips)
                        trashName.text = getString(R.string.info_metal_name)
                        basicInfo1.text = getString(R.string.info_metal_1)
                        basicInfo2.text = getString(R.string.info_metal_2)
                        basicInfo3.text = getString(R.string.info_metal_3)
                    }
                }
                else -> {
                    binding.apply {
                        replaceFragment(ViewPager2Fragment(Trash.PLASTIC))

                        wayRecyclerView.adapter =
                            InfoFragmentAdapter(mainViewModel.trash.value!![Trash.PLASTIC].ways)
                        tipRecyclerView.adapter =
                            InfoFragmentAdapter(mainViewModel.trash.value!![Trash.PLASTIC].tips)
                        trashName.text = getString(R.string.info_plastic_name)
                        basicInfo1.text = getString(R.string.info_plastic_1)
                        basicInfo2.text = getString(R.string.info_plastic_2)
                        basicInfo3.text = getString(R.string.info_plastic_3)
                    }
                }
            }
        })

        binding.apply {
            replaceFragment(ViewPager2Fragment(Trash.PLASTIC))

            wayRecyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            tipRecyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction: androidx.fragment.app.FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.infoFrameLayout, fragment)
        fragmentTransaction.commit()
    }

}