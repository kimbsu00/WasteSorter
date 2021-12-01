package com.butter.wastesorter.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.butter.wastesorter.R
import com.butter.wastesorter.adapter.RecordFragmentAdapter
import com.butter.wastesorter.data.Trash
import com.butter.wastesorter.databinding.FragmentRecordBinding
import com.butter.wastesorter.viewmodel.MainViewModel

class RecordFragment : Fragment() {

    val mainViewModel: MainViewModel by activityViewModels()

    lateinit var binding: FragmentRecordBinding

    lateinit var plasticAdapter: RecordFragmentAdapter
    lateinit var paperAdapter: RecordFragmentAdapter
    lateinit var cardboardAdapter: RecordFragmentAdapter
    lateinit var canAdapter: RecordFragmentAdapter
    lateinit var glassAdapter: RecordFragmentAdapter
    lateinit var metalAdapter: RecordFragmentAdapter
    lateinit var trashAdapter: RecordFragmentAdapter

    val rvVisible: ArrayList<Boolean> =
        arrayListOf(false, false, false, false, false, false, false)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordBinding.inflate(layoutInflater)

        init()

        return binding.root
    }

    private fun init() {
        mainViewModel.record.observe(viewLifecycleOwner, Observer { record ->
            val count: Int =
                record.plastic.size + record.paper.size + record.cardboard.size + record.can.size + record.glass.size + record.metal.size + record.trash.size
            binding.apply {
                totalCount.text = count.toString()
                plasticCount.text = record.plastic.size.toString()
                paperCount.text = record.paper.size.toString()
                cardboardCount.text = record.cardboard.size.toString()
                canCount.text = record.can.size.toString()
                glassCount.text = record.glass.size.toString()
                metalCount.text = record.metal.size.toString()
                trashCount.text = record.trash.size.toString()
            }

            plasticAdapter.notifyDataSetChanged()
            paperAdapter.notifyDataSetChanged()
            cardboardAdapter.notifyDataSetChanged()
            canAdapter.notifyDataSetChanged()
            glassAdapter.notifyDataSetChanged()
            metalAdapter.notifyDataSetChanged()
            trashAdapter.notifyDataSetChanged()
        })

        plasticAdapter = RecordFragmentAdapter(mainViewModel.record.value!!.plastic)
        paperAdapter = RecordFragmentAdapter(mainViewModel.record.value!!.paper)
        cardboardAdapter = RecordFragmentAdapter(mainViewModel.record.value!!.cardboard)
        canAdapter = RecordFragmentAdapter(mainViewModel.record.value!!.can)
        glassAdapter = RecordFragmentAdapter(mainViewModel.record.value!!.glass)
        metalAdapter = RecordFragmentAdapter(mainViewModel.record.value!!.metal)
        trashAdapter = RecordFragmentAdapter(mainViewModel.record.value!!.trash)

        binding.apply {
            plasticRV.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            plasticRV.adapter = plasticAdapter

            paperRV.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            paperRV.adapter = paperAdapter

            cardboardRV.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            cardboardRV.adapter = cardboardAdapter

            canRV.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            canRV.adapter = canAdapter

            glassRV.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            glassRV.adapter = glassAdapter

            metalRV.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            metalRV.adapter = metalAdapter

            trashRV.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            trashRV.adapter = trashAdapter

            plasticBtn.setOnClickListener {
                if (rvVisible[Trash.PLASTIC]) {
                    plasticBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                    plasticRV.visibility = View.GONE
                    rvVisible[Trash.PLASTIC] = false
                } else {
                    plasticBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                    plasticRV.visibility = View.VISIBLE
                    rvVisible[Trash.PLASTIC] = true
                }
            }

            paperBtn.setOnClickListener {
                if (rvVisible[Trash.PAPER]) {
                    paperBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                    paperRV.visibility = View.GONE
                    rvVisible[Trash.PAPER] = false
                } else {
                    paperBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                    paperRV.visibility = View.VISIBLE
                    rvVisible[Trash.PAPER] = true
                }
            }

            cardboardBtn.setOnClickListener {
                if (rvVisible[Trash.CARDBOARD]) {
                    cardboardBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                    cardboardRV.visibility = View.GONE
                    rvVisible[Trash.CARDBOARD] = false
                } else {
                    cardboardBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                    cardboardRV.visibility = View.VISIBLE
                    rvVisible[Trash.CARDBOARD] = true
                }
            }

            canBtn.setOnClickListener {
                if (rvVisible[Trash.CAN]) {
                    canBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                    canRV.visibility = View.GONE
                    rvVisible[Trash.CAN] = false
                } else {
                    canBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                    canRV.visibility = View.VISIBLE
                    rvVisible[Trash.CAN] = true
                }
            }

            glassBtn.setOnClickListener {
                if (rvVisible[Trash.GLASS]) {
                    glassBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                    glassRV.visibility = View.GONE
                    rvVisible[Trash.GLASS] = false
                } else {
                    glassBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                    glassRV.visibility = View.VISIBLE
                    rvVisible[Trash.GLASS] = true
                }
            }

            metalBtn.setOnClickListener {
                if (rvVisible[Trash.METAL]) {
                    metalBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                    metalRV.visibility = View.GONE
                    rvVisible[Trash.METAL] = false
                } else {
                    metalBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                    metalRV.visibility = View.VISIBLE
                    rvVisible[Trash.METAL] = true
                }
            }

            trashBtn.setOnClickListener {
                if (rvVisible[Trash.TRASH]) {
                    trashBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                    trashRV.visibility = View.GONE
                    rvVisible[Trash.TRASH] = false
                } else {
                    trashBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                    trashRV.visibility = View.VISIBLE
                    rvVisible[Trash.TRASH] = true
                }
            }
        }
    }

}