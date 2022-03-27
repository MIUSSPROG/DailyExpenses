package com.example.dailyexpenses.data

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyexpenses.api.ChildOfParent
import com.example.dailyexpenses.databinding.RvItemChildrenBinding


class ChildrenListAdapter: ListAdapter<ChildOfParent, ChildrenListAdapter.ChildrenListViewHolder>(DiffCallback()) {

    class ChildrenListViewHolder(private val binding: RvItemChildrenBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(childOfParent: ChildOfParent){
            binding.apply {
                tvChildLogin.text = childOfParent.login
                btnChildConfirm.setOnClickListener {
//                    Toast.makeText(binding.root.context, childOfParent.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<ChildOfParent>(){
        override fun areItemsTheSame(oldItem: ChildOfParent, newItem: ChildOfParent): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChildOfParent, newItem: ChildOfParent): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildrenListViewHolder {
        val binding = RvItemChildrenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChildrenListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChildrenListViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }
}