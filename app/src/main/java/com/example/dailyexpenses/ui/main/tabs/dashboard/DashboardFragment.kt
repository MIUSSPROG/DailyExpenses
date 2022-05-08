package com.example.dailyexpenses.ui.main.tabs.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyexpenses.R
import com.example.dailyexpenses.data.ItemToBuy
import com.example.dailyexpenses.data.ItemsToBuyAdapter
import com.example.dailyexpenses.databinding.FragmentDashboardBinding
import com.example.dailyexpenses.utils.HelperMethods.Companion.convertMillisToDateMills
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.properties.Delegates

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var bottomSheet: AddItemToBuyFragment
    private var selectedDateUnix by Delegates.notNull<Long>()
    private val itemsToBuyAdapter by lazy { ItemsToBuyAdapter() }
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(layoutInflater, container, false)

//        val events = mutableListOf<EventDay>()
//        val calendar = Calendar.getInstance()
//        val todayDay = convertMillisToDate(calendar.timeInMillis).split('/')[0].toInt()
//        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
//        val calendar1 = Calendar.getInstance()
//        calendar1.add(Calendar.DAY_OF_MONTH, 2)
//        val calendar2 = Calendar.getInstance()
//        calendar2.add(Calendar.DAY_OF_MONTH, lastDay-todayDay)
//        events.add(EventDay(calendar1, R.drawable.ic_question))
//        events.add(EventDay(calendar, R.drawable.ic_done))
//        events.add(EventDay(calendar2, R.drawable.ic_done))

//        viewModel.setCalendarEvents()

//        binding.calendarViewPlan.setEvents(events)

        binding.apply {
            val calendar = Calendar.getInstance()
            selectedDateUnix = convertMillisToDateMills(calendar.timeInMillis)
            viewModel.getItemsToBuy(pickedDate = selectedDateUnix)

            calendarViewPlan.setOnDayClickListener { eventDay ->
                selectedDateUnix = convertMillisToDateMills(eventDay.calendar.timeInMillis)
                viewModel.getItemsToBuy(pickedDate = selectedDateUnix)
            }

            btnAddDateItem.setOnClickListener{
                bottomSheet = AddItemToBuyFragment(selectedDateUnix)
                if (!bottomSheet.isAdded){
                    bottomSheet.show(childFragmentManager, "")
                }
            }

            btnSendParentToConfirm.setOnClickListener {
                viewModel.sendItemToBuyForParentApproval(selectedDateUnix)
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.itemsToBuy.collect {
                    when(it){
                        is DashboardViewModel.DashboardUiState.Success<*> -> {
                            itemsToBuyAdapter.submitList(it.data as List<ItemToBuy>)
                        }
                        is DashboardViewModel.DashboardUiState.Error -> {
                            Toast.makeText(requireContext(), "Не удалось получить данные!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                viewModel.plansChannelFlow.collect {
                    when(it){
                        is DashboardViewModel.DashboardUiState.Success<*> -> {
                            viewModel.getItemsToBuy(pickedDate = selectedDateUnix)
                            Toast.makeText(requireContext(), "Данные успешно отправлены на согласование!", Toast.LENGTH_SHORT).show()
                        }
                        is DashboardViewModel.DashboardUiState.Error -> {
                            Toast.makeText(requireContext(), "Данные уже были отправлены!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

//        lifecycleScope.launch {
//
//            viewModel.itemsToBuy.collect {
//                when(it){
//                    is DashboardViewModel.LoginUiState.Success<*> -> {
//                        itemsToBuyAdapter.submitList(it.data as List<ItemToBuy>)
//                    }
//                    is DashboardViewModel.LoginUiState.Error -> {
//                        Toast.makeText(requireContext(), "Не удалось получить данные!", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//
//            viewModel.plansChannelFlow.collect {
//                when(it){
//                    is DashboardViewModel.LoginUiState.Success<*> -> {
////                        viewModel.getItemsToBuy(pickedDate = selectedDateUnix)
//                        Toast.makeText(requireContext(), "Данные успешно отправлены на согласование!", Toast.LENGTH_SHORT).show()
//                    }
//                    is DashboardViewModel.LoginUiState.Error -> {
//                        Toast.makeText(requireContext(), "Данные уже были отправлены!", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }

//        viewModel.plansLiveData.observe(viewLifecycleOwner){ response ->
//            if (response == 400){
//                Toast.makeText(requireContext(), "Данные уже были отправлены!", Toast.LENGTH_SHORT).show()
//            }
//            else {
//                Toast.makeText(requireContext(), "Данные успешно отправлены на согласование!", Toast.LENGTH_SHORT).show()
//            }
//        }

        setupRecyclerViewSwipeToDelete()
        return binding.root
    }

//    private fun setCalendarEvents(){
//        val events = mutableListOf<EventDay>()
//        val calendar = Calendar.getInstance()
//        val todayDay = convertMillisToDate(calendar.timeInMillis).split('/')[0].toInt()
//        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

//        viewModel.getItemsFromServer()
//        viewModel.childPlansFromServer.observe(viewLifecycleOwner){ plans ->
//            var planGroupByDate = mutableMapOf<String, MutableList<Plan>>()
//            plans.forEach { plan ->
//                val date = convertMillisToDate(plan.date)
//                if (planGroupByDate[date] == null){
//                    planGroupByDate[date] = mutableListOf()
//                    planGroupByDate[date]!!.add(plan)
//                }else{
//                    planGroupByDate[date]!!.add(plan)
//                }
//                val calendar = Calendar.getInstance()
//                val curDay = convertMillisToDate(plan.date).split('/')[0].toInt()
//                calendar.add(Calendar.DAY_OF_MONTH, curDay-todayDay)
//                (plan.confirm).let {
//                    if (it == true){
//
//                    }
//                }
//                events.add(EventDay(calendar, R.drawable.ic_question))
//            }
//        }
//    }

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