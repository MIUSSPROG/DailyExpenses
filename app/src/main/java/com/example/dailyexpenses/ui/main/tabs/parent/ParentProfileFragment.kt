package com.example.dailyexpenses.ui.main.tabs.parent

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailyexpenses.R
import com.example.dailyexpenses.data.ChildrenListAdapter
import com.example.dailyexpenses.databinding.FragmentParentProfileBinding
import com.example.dailyexpenses.utils.findTopNavController
import com.example.dailyexpensespredprof.utils.prefs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ParentProfileFragment: Fragment(R.layout.fragment_parent_profile) {

    private lateinit var binding: FragmentParentProfileBinding
    private val childrenListAdapter by lazy { ChildrenListAdapter() }
    private val viewModel: ParentProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentParentProfileBinding.bind(view)
        binding.tvParentLogin.text = prefs.login
        setupRecyclerView()

        viewModel.getChildrenInvitations(prefs.id)
        viewModel.childrenInvitationsLiveData.observe(viewLifecycleOwner){ response ->
            if (response.isSuccessful) {
                childrenListAdapter.submitList(response.body())
            }
            else{
                Toast.makeText(requireContext(), "Ошибка!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnParentLogout.setOnClickListener {
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

    private fun setupRecyclerView(){
        binding.rvChildren.apply {
            adapter = childrenListAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }
}