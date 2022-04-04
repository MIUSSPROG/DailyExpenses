package com.example.dailyexpenses.ui.main.tabs.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.dailyexpenses.data.ItemToBuy
import com.example.dailyexpenses.databinding.FragmentAddItemToBuyBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddItemToBuyFragment(private val dateToBuyItemUnix: Long, private val dateToBuyItem: String): BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAddItemToBuyBinding
    private val viewModel: AddItemToBuyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddItemToBuyBinding.inflate(inflater, container, false)

        var selectedCategory = ""

        binding.apply {
            tvDateToBuy.text = dateToBuyItem
            val items = listOf("Кафе", "Продукты", "Транспорт", "Онлайн-покупки", "Канцелярия", "Кинотеатры")
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)

            etCategories.setAdapter(adapter)

            etCategories.setOnItemClickListener { adapterView, view, position, id ->
                selectedCategory = items[position]
            }

            btnSave.setOnClickListener {

                val plan = etPlans.text.toString()
                val sumToBuy = etSumToBuy.text.toString()

                if (selectedCategory != "" && plan.isNotBlank() && sumToBuy.isNotBlank()){
                    val itemToBuy = ItemToBuy(name = plan, price = sumToBuy.toFloat(), date = dateToBuyItemUnix, category = selectedCategory, confirm = null)
                    viewModel.saveItemToBuy(itemToBuy)
//                    Toast.makeText(requireContext(), "элемент $itemToBuy успешно сохранен!", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                else{
                    Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }
}