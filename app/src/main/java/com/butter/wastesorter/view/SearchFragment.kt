package com.butter.wastesorter.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.butter.wastesorter.R
import com.butter.wastesorter.adapter.SearchFragmentAdapter
import com.butter.wastesorter.data.Trash
import com.butter.wastesorter.databinding.FragmentSearchBinding
import com.butter.wastesorter.viewmodel.MainViewModel

class SearchFragment : Fragment() {

    val mainViewModel: MainViewModel by activityViewModels()

    lateinit var binding: FragmentSearchBinding

    val list: ArrayList<Trash> = ArrayList()
    lateinit var adapter: SearchFragmentAdapter

    var listener: OnFragmentInteraction? = null

    interface OnFragmentInteraction {
        fun showInfoFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)

        init()

        return binding.root
    }

    private fun init() {
        if (list.isEmpty()) {
            list.add(Trash("플라스틱", Trash.PLASTIC))
            list.add(Trash("종이", Trash.PAPER))
            list.add(Trash("상자", Trash.CARDBOARD))
            list.add(Trash("캔", Trash.CAN))
            list.add(Trash("유리", Trash.GLASS))
            list.add(Trash("철", Trash.METAL))
        }

        adapter = SearchFragmentAdapter(list, list)
        adapter.listener = object : SearchFragmentAdapter.OnItemClickListener {
            override fun onItemClicked(view: View, trash: Trash) {
                mainViewModel.setSelectedTrash(trash.code)
                this@SearchFragment.listener?.showInfoFragment()
            }
        }

        binding.apply {
            recyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = adapter
            recyclerView.itemAnimator = null
        }
    }

}