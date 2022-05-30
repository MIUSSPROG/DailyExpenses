package com.example.dailyexpenses.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyexpenses.R
import com.example.dailyexpenses.databinding.RvDateItemBinding

interface PhotoAttachmentActionListener{
    fun attachPhoto(itemToBuy: ItemToBuy)
    fun detachPhoto(itemToBuy: ItemToBuy)
}

class ItemsToBuyAdapter(private val actionListener: PhotoAttachmentActionListener): ListAdapter<ItemToBuy, ItemsToBuyAdapter.ItemToBuyViewHolder>(DiffCallback()), View.OnClickListener {

    class ItemToBuyViewHolder(private val binding: RvDateItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(itemToBuy: ItemToBuy){
            binding.apply {
                tvCategory.text = itemToBuy.category
                tvDateItem.text = itemToBuy.name
                tvDateItemPrice.text = itemToBuy.price.toString()
                attachPhotoLayout.visibility = View.GONE
                imgvAttachPhoto.tag = itemToBuy
                imgvDetachPhoto.tag = itemToBuy
                imgvAttachedPhoto.visibility = View.INVISIBLE
                if (itemToBuy.confirm == true) {
                    if (itemToBuy.imageUri != null){
                        imgvAttachedPhoto.visibility = View.VISIBLE
                    }
                    imgvConfirmStatus.setImageResource(R.drawable.ic_done)
                    attachPhotoLayout.visibility = View.VISIBLE
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
        binding.imgvAttachPhoto.setOnClickListener(this)
        binding.imgvDetachPhoto.setOnClickListener(this)
        return ItemToBuyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemToBuyViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    override fun onClick(v: View) {
        val itemToBuy = v.tag as ItemToBuy
        when(v.id){
            R.id.imgvAttachPhoto -> {
                actionListener.attachPhoto(itemToBuy)
            }
            R.id.imgvDetachPhoto -> {
                actionListener.detachPhoto(itemToBuy)
            }
        }
    }

}