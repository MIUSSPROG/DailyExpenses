package com.example.dailyexpenses.ui.main.tabs.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.navOptions
import com.example.dailyexpenses.R
import com.example.dailyexpenses.api.Parent
import com.example.dailyexpenses.data.ParentSearchAdapter
import com.example.dailyexpenses.databinding.FragmentProfileBinding
import com.example.dailyexpenses.ui.main.tabs.dashboard.DashboardViewModel
import com.example.dailyexpenses.utils.findTopNavController
import com.example.dailyexpensespredprof.utils.prefs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)


        viewModel.getParents()

        viewModel.parentsLiveData.observe(viewLifecycleOwner){ parents ->
            var adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, parents)
            binding.autoEtParentUsername.apply {
                setAdapter(adapter)
                threshold = 2
            }
        }

        binding.btnLogout.setOnClickListener {
            prefs.isSignedIn = false
            prefs.login = ""
            prefs.pass = ""
            prefs.role = ""
            findTopNavController().navigate(R.id.signInFragment, null, navOptions {
                popUpTo(R.id.tabsFragment){
                    inclusive = true
                }
            })
        }
    }
}