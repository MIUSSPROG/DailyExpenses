package com.example.dailyexpenses.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.example.dailyexpenses.api.Parent
import java.util.*

class ParentSearchAdapter(context: Context, @LayoutRes private val layoutResource: Int):
    ArrayAdapter<Parent>(context, layoutResource), Filterable {
        private var mParents: List<Parent> = emptyList()

    fun setData(parents: List<Parent>){
        mParents = parents
    }

    override fun getCount(): Int = mParents.size

    override fun getItem(position: Int): Parent? = mParents[position]

    override fun getItemId(position: Int): Long = mParents[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView as TextView? ?:
        LayoutInflater.from(context).inflate(layoutResource, parent, false) as TextView
        view.text = mParents[position].login
        return view
    }

    override fun getFilter(): Filter = object : Filter(){

        override fun performFiltering(charSequence: CharSequence?): FilterResults {

            var resList: List<Parent> = emptyList()

            val queryString = charSequence?.toString()?.lowercase(Locale.getDefault())

            val filterResults = FilterResults()

            if (queryString==null || queryString.isEmpty())
                resList = mParents
            else
                resList = mParents.filter {
                    it.login.lowercase(Locale.getDefault()).contains(queryString)
                }

            filterResults.values = resList
            filterResults.count = resList.count()
            return filterResults
        }

        override fun publishResults(p0: CharSequence?, filterResults: FilterResults?) {
            mParents = filterResults?.values as List<Parent>
            notifyDataSetChanged()
        }

    }
}