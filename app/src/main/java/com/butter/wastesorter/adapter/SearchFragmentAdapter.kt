package com.butter.wastesorter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.butter.wastesorter.R
import com.butter.wastesorter.data.Trash

class SearchFragmentAdapter(var items: ArrayList<Trash>, var filteredItems: ArrayList<Trash>) :
    RecyclerView.Adapter<SearchFragmentAdapter.ViewHolder>(), Filterable {

    var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClicked(view: View, trash: Trash)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var trashName: TextView

        init {
            trashName = itemView.findViewById(R.id.trashName)
            trashName.setOnClickListener {
                val pos: Int = adapterPosition
                listener?.onItemClicked(it, filteredItems[pos])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_search_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.trashName.text = filteredItems[position].name
    }

    override fun getItemCount(): Int {
        return filteredItems.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val str: String = constraint.toString()

                if (str.isBlank()) {
                    filteredItems = items
                } else {
                    val filteringList: ArrayList<Trash> = ArrayList()
                    for (trash in items) {
                        if (trash.name.contains(str)) {
                            filteringList.add(trash)
                        }
                    }
                    filteredItems = filteringList
                }
                val result: FilterResults = FilterResults()
                result.values = filteredItems

                return result
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null) {
                    filteredItems = results.values as ArrayList<Trash>
                    notifyDataSetChanged()
                }
            }

        }
    }
}