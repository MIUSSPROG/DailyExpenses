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
import com.example.dailyexpenses.data.DiagramData
import com.example.dailyexpenses.data.ItemToBuy
import com.example.dailyexpenses.databinding.FragmentDiagramBinding
import com.example.dailyexpenses.ui.main.tabs.dashboard.DashboardViewModel
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
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

        viewModel.dataForDiagram.observe(viewLifecycleOwner){
            showBarChart(it)
        }

        return binding.root
    }

    private fun showBarChart(data: List<DiagramData>){
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()
        data.forEachIndexed { index, diagramData ->
            entries.add(BarEntry(diagramData.date.toFloat()/10000000, diagramData.sumPrice))
            labels.add(diagramData.date.toString())
        }

        val barDataset = BarDataSet(entries, "Cells")
        val barData = BarData(barDataset)
        binding.itemsToBuyBarChart.data = barData
        binding.itemsToBuyBarChart.invalidate()
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
            val firstDateUnix = datePicked.first
            val secondDateUnix = datePicked.second
            val startDate = convertLongToDate(firstDateUnix)
            val secondDate = convertLongToDate(secondDateUnix)
            viewModel.getDataForDiagram(firstDateUnix, secondDateUnix)
            binding.tvDateRange.text = "$startDate - $secondDate"
        }
    }

    private fun convertLongToDate(time: Long): String{
        val date = Date(time)
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return format.format(date)
    }
}