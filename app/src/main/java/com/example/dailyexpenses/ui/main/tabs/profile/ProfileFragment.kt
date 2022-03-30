package com.example.dailyexpenses.ui.main.tabs.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navOptions
import com.example.dailyexpenses.R
import com.example.dailyexpenses.api.Parent
import com.example.dailyexpenses.data.ParentSearchAdapter
import com.example.dailyexpenses.databinding.FragmentProfileBinding
import com.example.dailyexpenses.ui.main.tabs.dashboard.DashboardViewModel
import com.example.dailyexpenses.utils.findTopNavController
import com.example.dailyexpensespredprof.utils.prefs
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.http.HTTP

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private var selectedParent: Parent? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        binding.tvLogin.text = prefs.login

        viewModel.getParents()

        viewModel.parentsLiveData.observe(viewLifecycleOwner){ parents ->
            var adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, parents)
            binding.autoEtParentUsername.apply {
                setAdapter(adapter)
                threshold = 2
                setOnItemClickListener { adapterView, view, pos, id ->
                    selectedParent = adapter.getItem(pos)
                }
            }
        }

        binding.btnSendInvitationToParent.setOnClickListener {
            binding.autoEtParentUsername.setText("")
            viewModel.checkInvitationLiveData.observe(viewLifecycleOwner){ code ->
                if (code == 200){
                    Toast.makeText(requireContext(), "Приглашение $selectedParent уже было отправлено", Toast.LENGTH_SHORT).show()
                }
            }
            if (selectedParent == null){
                val parentLoginToCheck = binding.autoEtParentUsername.text.toString()
                viewModel.checkParent(parentLoginToCheck)
            }else{
                viewModel.sendInvitation(selectedParent!!)
            }

            viewModel.checkParentLiveData.observe(viewLifecycleOwner){ parent ->
                if (parent == null){
                    Toast.makeText(requireContext(), "такого родителя не существует", Toast.LENGTH_SHORT).show()
                } else{
                    viewModel.sendInvitation(parent)
                }
            }

            viewModel.fcmLiveData.observe(viewLifecycleOwner){
                if (it.success == 1){
                    Toast.makeText(requireContext(), "Приглашение успешно отправлено!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnLogout.setOnClickListener {
            resetPrefs()
            findTopNavController().navigate(R.id.signInFragment, null, navOptions {
                popUpTo(R.id.tabsFragment){
                    inclusive = true
                }
            })
        }

    }

    private fun resetPrefs(){
        prefs.isSignedIn = false
        prefs.login = ""
        prefs.pass = ""
        prefs.role = ""
        prefs.id = 0
    }
}