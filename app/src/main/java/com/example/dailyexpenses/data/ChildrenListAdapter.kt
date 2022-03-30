package com.example.dailyexpenses.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyexpenses.R
import com.example.dailyexpenses.api.Child
import com.example.dailyexpenses.api.ChildInvitation
import com.example.dailyexpenses.api.ChildOfParent
import com.example.dailyexpenses.databinding.RvItemChildrenBinding
import com.example.dailyexpensespredprof.utils.prefs

interface ChildrenListActionListener{
    fun confirmInvitation(child: Child)
}

class ChildrenListAdapter(
    private val actionListener: ChildrenListActionListener
): ListAdapter<Child, ChildrenListAdapter.ChildrenListViewHolder>(DiffCallback()), View.OnClickListener {

    class ChildrenListViewHolder(private val binding: RvItemChildrenBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(child: Child){
            binding.apply {
//                itemView.tag = childOfParent
                btnChildConfirm.tag = child
                tvChildLogin.text = child.login
                if (child.confirmed){
                    btnChildConfirm.visibility = View.INVISIBLE
                    imgvChildConfirm.visibility = View.VISIBLE
                }
                else{
                    btnChildConfirm.visibility = View.VISIBLE
                    imgvChildConfirm.visibility = View.INVISIBLE
                }
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<Child>(){
        override fun areItemsTheSame(oldItem: Child, newItem: Child): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Child, newItem: Child): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildrenListViewHolder {
        val binding = RvItemChildrenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.btnChildConfirm.setOnClickListener(this)
        return ChildrenListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChildrenListViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    override fun onClick(v: View) {
        val child = v.tag as Child
        when(v.id){
            R.id.btnChildConfirm ->{
                actionListener.confirmInvitation(child)
                Toast.makeText(v.context, "Ребенок ${child.login} подтвержден!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}