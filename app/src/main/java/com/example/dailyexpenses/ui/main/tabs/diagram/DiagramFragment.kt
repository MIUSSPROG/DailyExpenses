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
import com.example.dailyexpenses.utils.HelperMethods.Companion.convertMillisToDate
import com.example.dailyexpenses.utils.XAxisDateFormatter
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
    private var firstDateUnixMillis: Long = 0
    private var secondDateUnixMillis: Long = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiagramBinding.inflate(layoutInflater, container, false)

        binding.btnSetDateRange.setOnClickListener { showDateRange() }


        viewModel.dataForDiagram.observe(viewLifecycleOwner){
            showBarChart(it)
        }

        return binding.root
    }

    private fun showBarChart(data: List<DiagramData>){
        val entries = mutableListOf<BarEntry>()
        val dayStart = convertMillisToDate(firstDateUnixMillis).split("/")[0].toFloat()
        val dayEnd = convertMillisToDate(secondDateUnixMillis).split("/")[0].toFloat()
        entries.add(BarEntry(dayStart, 0f))
        entries.add(BarEntry(dayEnd, 0f))
        data.forEachIndexed { index, diagramData ->
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
            viewModel.getDataForDiagram(firstDateUnixMillis, secondDateUnixMillis)
            binding.tvDateRange.text = "$startDate - $endDate"
        }
    }

//    private fun convertLongToDate(time: Long): String{
//        val date = Date(time)
//        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//        return format.format(date)
//    }
}