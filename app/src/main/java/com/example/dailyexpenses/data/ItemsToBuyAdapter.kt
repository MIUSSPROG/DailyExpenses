package com.example.dailyexpenses.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyexpenses.R
import com.example.dailyexpenses.databinding.RvDateItemBinding

class ItemsToBuyAdapter: ListAdapter<ItemToBuy, ItemsToBuyAdapter.ItemToBuyViewHolder>(DiffCallback()) {

    class ItemToBuyViewHolder(private val binding: RvDateItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(itemToBuy: ItemToBuy){
            binding.apply {
                tvCategory.text = itemToBuy.category
                tvDateItem.text = itemToBuy.name
                tvDateItemPrice.text = itemToBuy.price.toString()
                if (itemToBuy.confirm == true) {
                    imgvConfirmStatus.setImageResource(R.drawable.ic_done)
                }
                else if (itemToBuy.confirm == false){
                    imgvConfirmStatus.setImageResource(R.drawable.ic_cancel)
                }
                else if (itemToBuy.send){
                    imgvConfirmStatus.setImageResource(R.drawable.ic_sync)
                }
                else{
                    imgvConfirmStatus.setImageDrawable(null)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ItemToBuy>(){
        override fun areItemsTheSame(oldItem: ItemToBuy, newItem: ItemToBuy): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ItemToBuy, newItem: ItemToBuy): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemToBuyViewHolder {
        val binding = RvDateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemToBuyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemToBuyViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

}