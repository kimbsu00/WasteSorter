package com.butter.wastesorter.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.butter.wastesorter.adapter.SearchFragmentAdapter
import com.butter.wastesorter.data.Trash
import com.butter.wastesorter.databinding.FragmentSearchBinding
import com.butter.wastesorter.viewmodel.MainViewModel

class SearchFragment : Fragment() {

    val mainViewModel: MainViewModel by activityViewModels()

    lateinit var binding: FragmentSearchBinding

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
        adapter = SearchFragmentAdapter(mainViewModel.trash.value!!, mainViewModel.trash.value!!)
        adapter.listener = object : SearchFragmentAdapter.OnItemClickListener {
            override fun onItemClicked(view: View, trash: Trash) {
                mainViewModel.selectedTrash.value = trash.code
                this@SearchFragment.listener?.showInfoFragment()
            }
        }

        binding.apply {
            recyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = adapter
            recyclerView.itemAnimator = null

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    adapter.filter.filter(query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter.filter(newText)
                    return true
                }
            })
        }
    }

}