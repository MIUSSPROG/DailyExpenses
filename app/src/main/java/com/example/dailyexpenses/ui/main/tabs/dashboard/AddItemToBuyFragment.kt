package com.example.dailyexpenses.ui.main.tabs.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.dailyexpenses.api.Category
import com.example.dailyexpenses.data.ItemToBuy
import com.example.dailyexpenses.databinding.FragmentAddItemToBuyBinding
import com.example.dailyexpenses.utils.HelperMethods
import com.example.dailyexpenses.utils.prefs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat

@AndroidEntryPoint
class AddItemToBuyFragment: BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAddItemToBuyBinding
    private val viewModel: AddItemToBuyViewModel by viewModels()
    private lateinit var categories: List<Category>
    private var selectedCategory: Category? = null
    private val args: AddItemToBuyFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddItemToBuyBinding.inflate(inflater, container, false)
//        private val dateToBuyItem: Long
        binding.apply {
            tvDateToBuy.text = HelperMethods.convertMillisToDate(args.dateToBuyItem)

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
                    val itemToBuy = ItemToBuy(userRemoteId = prefs.id, name = plan, price = sumToBuy.toFloat(), date = args.dateToBuyItem, category = selectedCategory!!.name, categoryId = selectedCategory!!.id, confirm = null)
                    viewModel.saveItemToBuy(itemToBuy)

                    parentFragmentManager.setFragmentResult(REQUEST_CODE, bundleOf(EXTRA_DATE_SELECTED to args.dateToBuyItem))
                    dismiss()
                }
                else{
                    Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }

    companion object{
        const val REQUEST_CODE = "SAVE_ITEM_REQUEST_CODE"
        const val EXTRA_DATE_SELECTED = "EXTRA_DATE_SELECTED"
    }

}