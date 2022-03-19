package com.example.dailyexpenses.ui.main.tabs.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyexpenses.R
import com.example.dailyexpenses.data.ItemsToBuyAdapter
import com.example.dailyexpenses.databinding.FragmentAddItemToBuyBinding
import com.example.dailyexpenses.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates
import kotlin.time.days

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var bottomSheet: AddItemToBuyFragment
    private var selectedDate: String? = null
    private var selectedDateUnix by Delegates.notNull<Long>()
    private val itemsToBuyAdapter by lazy { ItemsToBuyAdapter() }
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(layoutInflater, container, false)

        binding.apply {
            val currentSelectedDate = calendarViewPlan.date
            val sdf = SimpleDateFormat("dd/M/yyyy")
            selectedDate = sdf.format(currentSelectedDate)
            viewModel.getItemsToBuy(pickedDate = selectedDate!!)

            calendarViewPlan.setOnDateChangeListener { calendarView, year, month, dayOfMonth ->
                selectedDate = "$dayOfMonth/${month+1}/$year"
                viewModel.getItemsToBuy(pickedDate = selectedDate!!)
            }

            btnAddDateItem.setOnClickListener{
                selectedDateUnix = SimpleDateFormat("dd/M/yyyy").parse(selectedDate).time
                bottomSheet = AddItemToBuyFragment(selectedDateUnix, selectedDate ?: "")
                if (!bottomSheet.isAdded){
                    bottomSheet.show(childFragmentManager, "")
                }
            }

        }

        viewModel.itemToBuyLiveData.observe(viewLifecycleOwner){
            itemsToBuyAdapter.submitList(it)
        }

        setupRecyclerViewSwipeToDelete()
        return binding.root
    }


    private fun setupRecyclerViewSwipeToDelete(){
        binding.rvDateItems.apply {
            adapter = itemsToBuyAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        val itemTouchHelper = ItemTouchHelper(object: ItemTouchHelper.Callback(){
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val swipeFlags = ItemTouchHelper.START
                val dragFlags = ItemTouchHelper.DOWN
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val itemToDelete = itemsToBuyAdapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteItem(itemToDelete)
                Toast.makeText(requireContext(), "Элемент удален!", Toast.LENGTH_SHORT).show()
            }

        })

        itemTouchHelper.attachToRecyclerView(binding.rvDateItems)
    }

}