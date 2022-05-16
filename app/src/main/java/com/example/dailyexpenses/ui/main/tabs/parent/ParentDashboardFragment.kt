package com.example.dailyexpenses.ui.main.tabs.parent

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailyexpenses.R
import com.example.dailyexpenses.api.Child
import com.example.dailyexpenses.api.PlanRV
import com.example.dailyexpenses.data.ChildPlanActionListener
import com.example.dailyexpenses.data.ChildPlanAdapter
import com.example.dailyexpenses.databinding.FragmentParentDashboardBinding
import com.example.dailyexpenses.utils.HelperMethods
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ParentDashboardFragment : Fragment(R.layout.fragment_parent_dashboard) {

    private lateinit var binding: FragmentParentDashboardBinding
    private val viewModel: ParentDashboardViewModel by viewModels()
    private lateinit var children: List<Child>
    private var selectedChild: Child? = null
    private lateinit var childPlanAdapter: ChildPlanAdapter
    private var firstDateUnixMillis: Long = 0
    private var secondDateUnixMillis: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentParentDashboardBinding.bind(view)
        binding.apply {

            btnCancelPeriod.setOnClickListener {
                firstDateUnixMillis = 0
                secondDateUnixMillis = 0
                tvDateRangeParent.text = ""
                switchStatusHandle()
                allCheckedHandle()
            }

            btnChoosePeriod.setOnClickListener {
                val dateRangePicker = MaterialDatePicker.Builder
                    .dateRangePicker()
                    .setTitleText("Выберите диапазон")
                    .build()

                dateRangePicker.show(
                    parentFragmentManager, "date_range_picker"
                )

                dateRangePicker.addOnPositiveButtonClickListener { datePicked ->
                    firstDateUnixMillis = datePicked.first
                    secondDateUnixMillis = datePicked.second
                    val startDate = HelperMethods.convertMillisToDate(firstDateUnixMillis)
                    val endDate = HelperMethods.convertMillisToDate(secondDateUnixMillis)
                    tvDateRangeParent.text = "$startDate - $endDate"
                    switchStatusHandle()
                    allCheckedHandle()
                }
            }

            switchStatus.setOnClickListener { switchStatusHandle() }
            cbAllChecked.setOnClickListener { allCheckedHandle() }

            swipeToRefreshParent.setOnRefreshListener {
                swipeToRefreshParent.setColorSchemeColors(resources.getColor(R.color.color1))
                if (switchStatus.isEnabled){
                    selectedChild?.let { child -> viewModel.filterPlans(child.id, switchStatus.isChecked, firstDateUnixMillis, secondDateUnixMillis) }
                    swipeToRefreshParent.isRefreshing = false
                }
                else {
                    selectedChild?.let { viewModel.getChildrenPlans(it.id, firstDateUnixMillis, secondDateUnixMillis) }
                    swipeToRefreshParent.isRefreshing = false
                }
            }
        }

        childPlanAdapter = ChildPlanAdapter(object : ChildPlanActionListener {
            override fun confirmPlan(planRV: PlanRV) {
                viewModel.confirmChildPlan(planRV)
            }

            override fun rejectPlan(planRV: PlanRV) {
                viewModel.rejectChildPlan(planRV)
            }
        })

        viewModel.planRejected.observe(viewLifecycleOwner) { planRejected ->
            if (planRejected) {
                viewModel.getChildrenPlans(selectedChild!!.id, firstDateUnixMillis, secondDateUnixMillis)
                Toast.makeText(requireContext(), "Запрос отклонен", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Ошибка отклонения", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.planConfirmed.observe(viewLifecycleOwner) { planConfirmed ->
            if (planConfirmed) {
                viewModel.getChildrenPlans(selectedChild!!.id, firstDateUnixMillis, secondDateUnixMillis)
                Toast.makeText(requireContext(), "Запрос подтвержден", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Ошибка подтверждения", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.getChildren()
        viewModel.childrenLiveData.observe(viewLifecycleOwner) { parentChildren ->
            parentChildren?.let {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    it.children
                )
                children = parentChildren.children
                binding.actvChildrenToChoose.setAdapter(adapter)
            }
        }

        binding.actvChildrenToChoose.setOnItemClickListener { adapterView, view, position, id ->
            selectedChild = children[position]
            viewModel.getChildrenPlans(selectedChild!!.id, firstDateUnixMillis, secondDateUnixMillis)
        }

        viewModel.childPlans.observe(viewLifecycleOwner) {
                childPlanAdapter.submitList(it)
        }

        binding.rvChildPlans.apply {
            adapter = childPlanAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun allCheckedHandle() {
        binding.apply {
            if (cbAllChecked.isChecked) {
                switchStatus.isEnabled = false
                selectedChild?.let { child ->
                    viewModel.getChildrenPlans(
                        child.id,
                        firstDateUnixMillis,
                        secondDateUnixMillis
                    )
                }
            } else {
                switchStatus.isEnabled = true
                selectedChild?.let { child ->
                    viewModel.filterPlans(
                        child.id,
                        switchStatus.isChecked,
                        firstDateUnixMillis,
                        secondDateUnixMillis
                    )
                }
            }
        }
    }

    private fun switchStatusHandle() {
        binding.apply {
            if (switchStatus.isChecked) {
                switchStatus.text = "одобренные"
            } else {
                switchStatus.text = "отклоненные"
            }
            selectedChild?.let { child ->
                viewModel.filterPlans(
                    child.id,
                    switchStatus.isChecked,
                    firstDateUnixMillis,
                    secondDateUnixMillis
                )
            }
        }
    }

//    companion object{
//        const val ACCEPTED = "одобренные"
//        const val REJECTED = "отклоненные"
//        const val ALL = "все"
//    }
}