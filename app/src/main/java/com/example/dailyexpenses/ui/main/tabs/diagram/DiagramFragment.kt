package com.example.dailyexpenses.ui.main.tabs.diagram

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.dailyexpenses.R
import com.example.dailyexpenses.api.Child
import com.example.dailyexpenses.databinding.FragmentDiagramBinding
import com.example.dailyexpenses.ui.main.tabs.dashboard.DashboardViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DiagramFragment : Fragment(R.layout.fragment_diagram) {

    private lateinit var binding: FragmentDiagramBinding
    private val viewModel: DiagramViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiagramBinding.inflate(layoutInflater, container, false)

        binding.btnSetDateRange.setOnClickListener { showDateRange() }

//        binding.btnCreateChild.setOnClickListener {
//
//            val result = viewModel.createChild(Child(login = "android", parentId = 1))
//            Toast.makeText(requireContext(), "ребенок успешно добавлен! result: $result", Toast.LENGTH_SHORT).show()
//        }

        return binding.root
    }

    private fun showDateRange() {
        val dateRangePicker = MaterialDatePicker.Builder
            .dateRangePicker()
            .setTitleText("Выберите диапазон")
            .build()

        dateRangePicker.show(
            parentFragmentManager, "date_range_picker"
        )

        dateRangePicker.addOnPositiveButtonClickListener { datePicked ->
            val startDate = convertLongToDate(datePicked.first)
            val secondDate = convertLongToDate(datePicked.second)
            binding.tvDateRange.text = "$startDate - $secondDate"
        }
    }

    private fun convertLongToDate(time: Long): String{
        val date = Date(time)
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return format.format(date)
    }
}