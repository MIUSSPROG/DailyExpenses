package com.example.dailyexpenses.ui.main.tabs.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.dailyexpenses.api.Category
import com.example.dailyexpenses.data.ItemToBuy
import com.example.dailyexpenses.databinding.FragmentAddItemToBuyBinding
import com.example.dailyexpenses.utils.HelperMethods
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat

@AndroidEntryPoint
class AddItemToBuyFragment(private val dateToBuyItem: Long): BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAddItemToBuyBinding
    private val viewModel: AddItemToBuyViewModel by viewModels()
    private lateinit var categories: List<Category>
    private var selectedCategory: Category? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddItemToBuyBinding.inflate(inflater, container, false)

        binding.apply {
            tvDateToBuy.text = HelperMethods.convertMillisToDate(dateToBuyItem)

            viewModel.getCategories()
            viewModel.categoriesLiveData.observe(viewLifecycleOwner){
                categories = it
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, it)
                etCategories.setAdapter(adapter)
            }


            etCategories.setOnItemClickListener { adapterView, view, position, id ->
                selectedCategory = categories[position]
            }

            btnSave.setOnClickListener {

                val plan = etPlans.text.toString()
                val sumToBuy = etSumToBuy.text.toString()

                if (selectedCategory != null && plan.isNotBlank() && sumToBuy.isNotBlank()){
                    val itemToBuy = ItemToBuy(name = plan, price = sumToBuy.toFloat(), date = dateToBuyItem, category = selectedCategory!!.name, categoryId = selectedCategory!!.id, confirm = null)
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