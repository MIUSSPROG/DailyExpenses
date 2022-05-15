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
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.example.dailyexpenses.R
import com.example.dailyexpenses.data.ItemToBuy
import com.example.dailyexpenses.data.ItemsToBuyAdapter
import com.example.dailyexpenses.databinding.FragmentDashboardBinding
import com.example.dailyexpenses.ui.main.tabs.dashboard.AddItemToBuyFragment.Companion.EXTRA_DATE_SELECTED
import com.example.dailyexpenses.ui.main.tabs.dashboard.AddItemToBuyFragment.Companion.REQUEST_CODE
import com.example.dailyexpenses.utils.HelperMethods
import com.example.dailyexpenses.utils.HelperMethods.Companion.convertMillisToDateMills
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.properties.Delegates

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private lateinit var binding: FragmentDashboardBinding
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

            val calendar = Calendar.getInstance()
            val curMonth = HelperMethods.convertMillisToDate(calendar.timeInMillis).split('/')[1].toInt()
            viewModel.setCalendarEvents(curMonth)
            viewModel.calendarEvents.observe(viewLifecycleOwner){
                calendarViewPlan.setEvents(it)
            }

            selectedDateUnix = convertMillisToDateMills(calendar.timeInMillis)

            viewModel.getItemsToBuy(pickedDate = selectedDateUnix)

            calendarViewPlan.setOnDayClickListener { eventDay ->
                selectedDateUnix = convertMillisToDateMills(eventDay.calendar.timeInMillis)
                viewModel.getItemsToBuy(pickedDate = selectedDateUnix)
            }

            parentFragmentManager.setFragmentResultListener(
                REQUEST_CODE,
                viewLifecycleOwner
            ) { _, data ->
                selectedDateUnix = data.getLong(EXTRA_DATE_SELECTED)
                viewModel.getItemsToBuy(pickedDate = selectedDateUnix)
            }

            btnAddItem.setOnClickListener {
                val action =
                    DashboardFragmentDirections.navigateToAddItemToBuyFragment(selectedDateUnix)
                findNavController().navigate(action)
            }


            btnSendParentToConfirm.setOnClickListener {
                viewModel.sendItemToBuyForParentApproval(selectedDateUnix)
            }

            swipeToRefresh.setColorSchemeColors(resources.getColor(R.color.color1))
            swipeToRefresh.setOnRefreshListener {
                viewModel.getItemsToBuy(pickedDate = selectedDateUnix)
                swipeToRefresh.isRefreshing = false
            }
        }

        viewModel.itemsToBuy.observe(viewLifecycleOwner) {
            when (it) {
                is DashboardViewModel.DashboardUiState.Success<*> -> {
                    itemsToBuyAdapter.submitList(it.data as List<ItemToBuy>)
                    val calendar = Calendar.getInstance()
                    val curMonth = HelperMethods.convertMillisToDate(calendar.timeInMillis).split('/')[1].toInt()
                    viewModel.setCalendarEvents(curMonth)
                }
                is DashboardViewModel.DashboardUiState.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "Не удалось получить данные!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.plansChannelFlow.collect {
                    when (it) {
                        is DashboardViewModel.DashboardUiState.Success<*> -> {
                            viewModel.getItemsToBuy(pickedDate = selectedDateUnix)
                            Toast.makeText(
                                requireContext(),
                                "Данные успешно отправлены на согласование!",
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.getItemsToBuy(pickedDate = selectedDateUnix)
                        }
                        is DashboardViewModel.DashboardUiState.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "Данные уже были отправлены!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }


        setupRecyclerViewSwipeToDelete()
        return binding.root
    }


    private fun setupRecyclerViewSwipeToDelete() {
        binding.rvDateItems.apply {
            adapter = itemsToBuyAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        viewModel.deletedItem.observe(viewLifecycleOwner){
            when(it){
                is DashboardViewModel.DashboardUiState.Success<*> -> {
                    Toast.makeText(requireContext(), "Элемент удален!", Toast.LENGTH_SHORT).show()
                    viewModel.getItemsToBuy(pickedDate = selectedDateUnix)
                }
                is DashboardViewModel.DashboardUiState.Error -> {
                    Toast.makeText(requireContext(), "Ошибка!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
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
                val calendar = Calendar.getInstance()
                val curMonth = HelperMethods.convertMillisToDate(calendar.timeInMillis).split('/')[1].toInt()
                viewModel.deleteItem(itemToDelete)
                viewModel.setCalendarEvents(curMonth)
            }

        })

        itemTouchHelper.attachToRecyclerView(binding.rvDateItems)
    }

}