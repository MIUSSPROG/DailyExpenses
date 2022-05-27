package com.example.dailyexpenses.ui.main.tabs.diagram

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.dailyexpenses.R
import com.example.dailyexpenses.data.DiagramData
import com.example.dailyexpenses.data.HistogramData
import com.example.dailyexpenses.databinding.FragmentDiagramBinding
import com.example.dailyexpenses.utils.HelperMethods.Companion.convertMillisToDate
import com.example.dailyexpenses.utils.UiState
import com.example.dailyexpenses.utils.XAxisDateFormatter
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiagramFragment: Fragment(R.layout.fragment_diagram) {

    private lateinit var binding: FragmentDiagramBinding
    private val viewModel: DiagramViewModel by viewModels()
    private var firstDateUnixMillis: Long = 0
    private var secondDateUnixMillis: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDiagramBinding.bind(view)

        binding.btnSetDateRange.setOnClickListener { showDateRange() }

        viewModel.dataForHistogram.observe(viewLifecycleOwner){
            when(it){
                is UiState.Success -> {
                    it.data?.let { it1 -> showBarChart(it1) }
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), "Ошибка получения данных для гистограммы!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.dataForDiagram.observe(viewLifecycleOwner){
            when(it){
                is UiState.Success -> {
                    it.data?.let { it1 -> showPieChart(it1) }
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), "Ошибка получения данных для диаграммы!", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun showPieChart(data: List<DiagramData>){
        val entries = mutableListOf<PieEntry>()
        data.forEach { diagramData ->
            entries.add(PieEntry(diagramData.categoryId.toFloat(), diagramData.sumPrice))
        }
        val pieDataset = PieDataSet(entries, "PieCells")
        pieDataset.colors = ColorTemplate.MATERIAL_COLORS.toList()
        val pieData = PieData(pieDataset)
//        pieData.setValueFormatter(PieAxisCategoryNameFormatter())
        binding.itemsToBuyPieChart.data = pieData
        binding.itemsToBuyPieChart.animateY(1000)
        binding.itemsToBuyPieChart.invalidate()
    }

    private fun showBarChart(data: List<HistogramData>){
        val entries = mutableListOf<BarEntry>()
        val dayStart = convertMillisToDate(firstDateUnixMillis).split("/")[0].toFloat()
        val dayEnd = convertMillisToDate(secondDateUnixMillis).split("/")[0].toFloat()
        entries.add(BarEntry(dayStart, 0f))
        entries.add(BarEntry(dayEnd, 0f))
        data.forEach{ diagramData ->
            val day = convertMillisToDate(diagramData.date).split("/")[0].toFloat()
            entries.add(BarEntry(day, diagramData.sumPrice))
        }

        val barDataset = BarDataSet(entries, "Cells")
        barDataset.colors = ColorTemplate.MATERIAL_COLORS.toList()
        val barData = BarData(barDataset)
        binding.itemsToBuyBarChart.data = barData
//        binding.itemsToBuyBarChart.xAxis.valueFormatter = XAxisDateFormatter()
        binding.itemsToBuyBarChart.animateY(1000)
        binding.itemsToBuyBarChart.setFitBars(true)
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
            firstDateUnixMillis = datePicked.first
            secondDateUnixMillis = datePicked.second
            val startDate = convertMillisToDate(firstDateUnixMillis)
            val endDate = convertMillisToDate(secondDateUnixMillis)
            viewModel.getDataForHistogram(firstDateUnixMillis, secondDateUnixMillis)
            viewModel.getDataForDiagram(firstDateUnixMillis, secondDateUnixMillis)
            binding.tvDateRange.text = "$startDate - $endDate"
        }
    }

}