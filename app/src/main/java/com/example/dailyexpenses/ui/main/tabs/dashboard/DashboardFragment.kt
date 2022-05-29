package com.example.dailyexpenses.ui.main.tabs.dashboard

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.*
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
import com.example.dailyexpenses.data.PhotoAttachmentActionListener
import com.example.dailyexpenses.databinding.FragmentDashboardBinding
import com.example.dailyexpenses.ui.main.tabs.dashboard.AddItemToBuyFragment.Companion.EXTRA_DATE_SELECTED
import com.example.dailyexpenses.ui.main.tabs.dashboard.AddItemToBuyFragment.Companion.REQUEST_CODE
import com.example.dailyexpenses.utils.HelperMethods
import com.example.dailyexpenses.utils.HelperMethods.Companion.convertMillisToDateMills
import com.example.dailyexpenses.utils.HelperMethods.Companion.getRealPathFromURI
import com.example.dailyexpenses.utils.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.lang.Math.abs
import java.util.*
import kotlin.properties.Delegates

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private lateinit var binding: FragmentDashboardBinding
    private var selectedDateUnix by Delegates.notNull<Long>()
    private var isCurMonth = true
    private var curMonth by Delegates.notNull<Int>()
    private var selectedItemToBuy: ItemToBuy? = null
    private val viewModel: DashboardViewModel by viewModels()

    private var resultLauncher = registerForActivityResult(StartActivityForResult()){
        if (it.resultCode == RESULT_OK && it.data!= null && it.data!!.data != null){
            val uri = it.data!!.data!!
//            val realPath = getRealPathFromURI(requireContext(), uri)
            val realPath = RealPathUtil.getRealPath(requireContext(), uri)
//            Log.d("path", realPath.toString())
//            Log.d("path", realPath2.toString())
//            val bitmap = BitmapFactory.decodeFile(realPath)
//            Log.d("bitmap", bitmap.toString())
//            Log.d("uri", getRealPathFromURI(requireContext(), uri))
//            Log.d("bitmap", uri.path!!)
//            var os = ByteArrayOutputStream()
//            var inputStream = requireActivity().contentResolver.openInputStream(uri)
//            if (inputStream != null) {
//                var byteArray = inputStream.readBytes()
            selectedItemToBuy?.let { it1 -> viewModel.updateItem(it1.copy(imageUri = realPath))}
//            }
        }
    }

    private val requestReadStoragePermissionLauncher = registerForActivityResult(
        RequestPermission(),
        ::onGotReadStoragePermission
    )

    private fun onGotReadStoragePermission(granted: Boolean){
        if (granted){
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            resultLauncher.launch(intent)
        }else{
            if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                askUserForOpeningAppSettings()
            }else{
                Toast.makeText(requireContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun askUserForOpeningAppSettings(){
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", requireActivity().packageName, null)
        )
        if (requireActivity().packageManager.resolveActivity(appSettingsIntent, PackageManager.MATCH_DEFAULT_ONLY) == null){
            Toast.makeText(requireContext(), R.string.permissions_denied_forever, Toast.LENGTH_SHORT).show()
        }else{
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.permission_denied)
                .setMessage(R.string.permission_denied_forever_message)
                .setPositiveButton(R.string.open){ _, _ ->
                    startActivity(appSettingsIntent)
                }.create().show()
        }
    }

    private val itemsToBuyAdapter by lazy { ItemsToBuyAdapter(object:
        PhotoAttachmentActionListener {
        override fun attachPhoto(itemToBuy: ItemToBuy) {
            selectedItemToBuy = itemToBuy
            requestReadStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        override fun detachPhoto(itemToBuy: ItemToBuy) {
            viewModel.updateItem(itemToBuy.copy(imageUri = null))
        }

    }) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(layoutInflater, container, false)

        binding.apply {

            val calendar = Calendar.getInstance()
            curMonth = HelperMethods.convertMillisToDate(calendar.timeInMillis).split('/')[1].toInt()
            viewModel.setCalendarEvents(curMonth)
            viewModel.calendarEvents.observe(viewLifecycleOwner) {
                calendarViewPlan.setEvents(it)
            }

            viewModel.updatedItem.observe(viewLifecycleOwner){
                when(it){
                    is UiState.Success -> {
                        Toast.makeText(requireContext(), "Успешно!", Toast.LENGTH_SHORT).show()
                    }
                    is UiState.Error -> {
                        Toast.makeText(requireContext(), "Ошибка прикрепления/открепления фото!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            selectedDateUnix = convertMillisToDateMills(calendar.timeInMillis + 24*60*60*1000)

            viewModel.getItemsToBuy(pickedDate = selectedDateUnix)

            calendarViewPlan.setOnForwardPageChangeListener {
                curMonth ++
                if (curMonth == 13){
                    curMonth = 1
                }
                isCurMonth = false
                viewModel.setCalendarEvents(curMonth)
            }

            calendarViewPlan.setOnPreviousPageChangeListener {
                curMonth--
                if (curMonth == 0){
                    curMonth = 12
                }
                isCurMonth = false
                viewModel.setCalendarEvents(curMonth)
            }

            calendarViewPlan.setOnDayClickListener { eventDay ->
                selectedDateUnix = convertMillisToDateMills(eventDay.calendar.timeInMillis + 24*60*60*1000)
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
                Log.d("time", selectedDateUnix.toString())
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
                is UiState.Success -> {
                    itemsToBuyAdapter.submitList(it.data as List<ItemToBuy>)
                    viewModel.setCalendarEvents(curMonth)
                }
                is UiState.Error -> {
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
                        is UiState.Success -> {
                            viewModel.getItemsToBuy(pickedDate = selectedDateUnix)
                            Toast.makeText(
                                requireContext(),
                                "Данные успешно отправлены на согласование!",
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.getItemsToBuy(pickedDate = selectedDateUnix)
                        }
                        is UiState.Error -> {
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deletedItemChannelFlow.collect {
                    when(it){
                        is UiState.Success -> {
                            Toast.makeText(requireContext(), "Элемент удален!", Toast.LENGTH_SHORT).show()
                            viewModel.getItemsToBuy(pickedDate = selectedDateUnix)
                        }
                        is UiState.Error -> {
                            Toast.makeText(requireContext(), "Ошибка!", Toast.LENGTH_SHORT).show()
                        }
                    }
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
                viewModel.deleteItem(itemToDelete)
            }

        })

        itemTouchHelper.attachToRecyclerView(binding.rvDateItems)
    }

}