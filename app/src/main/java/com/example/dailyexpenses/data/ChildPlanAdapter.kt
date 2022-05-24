package com.example.dailyexpenses.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyexpenses.R
import com.example.dailyexpenses.api.Plan
import com.example.dailyexpenses.api.PlanRV
import com.example.dailyexpenses.databinding.RvItemChildPlanBinding
import com.example.dailyexpenses.utils.HelperMethods

interface ChildPlanActionListener{
    fun confirmPlan(planRV: PlanRV)
    fun rejectPlan(planRV: PlanRV)
}

class ChildPlanAdapter(
    private val actionListener: ChildPlanActionListener
): ListAdapter<PlanRV, ChildPlanAdapter.ChildPlanViewHolder>(DiffCallback()), View.OnClickListener {

    class ChildPlanViewHolder(private val binding: RvItemChildPlanBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(plan: PlanRV){
            binding.apply {
                tvPlanCat.text = plan.categoryName
                tvPlanItemDate.text = HelperMethods.convertMillisToDate(plan.date - 24*60*60*1000)
                tvPlanItemName.text = plan.name
                tvPlanItemPrice.text = plan.price.toString()
                btnConfirmPlanItem.tag = plan
                btnRejectPlanItem.tag = plan
                if (plan.confirm == true){
                    imgvStatus.setImageResource(R.drawable.ic_done)
                }else if(plan.confirm == false){
                    imgvStatus.setImageResource(R.drawable.ic_rejected)
                }
                else{
                    imgvStatus.setImageResource(R.drawable.ic_waiting)
                }

            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<PlanRV>(){
        override fun areItemsTheSame(oldItem: PlanRV, newItem: PlanRV): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlanRV, newItem: PlanRV): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildPlanViewHolder {
        val binding = RvItemChildPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.btnConfirmPlanItem.setOnClickListener(this)
        binding.btnRejectPlanItem.setOnClickListener(this)
        return ChildPlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChildPlanViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    override fun onClick(v: View) {
        val plan = v.tag as PlanRV
        when(v.id){
            R.id.btnConfirmPlanItem -> {
                actionListener.confirmPlan(plan)
            }
            R.id.btnRejectPlanItem -> {
                actionListener.rejectPlan(plan)
            }
        }
    }
}