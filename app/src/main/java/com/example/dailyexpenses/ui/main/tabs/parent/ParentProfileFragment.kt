package com.example.dailyexpenses.ui.main.tabs.parent

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailyexpenses.R
import com.example.dailyexpenses.api.Child
import com.example.dailyexpenses.data.ChildrenListActionListener
import com.example.dailyexpenses.data.ChildrenListAdapter
import com.example.dailyexpenses.databinding.FragmentParentProfileBinding
import com.example.dailyexpenses.utils.findTopNavController
import com.example.dailyexpensespredprof.utils.prefs
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.notify

@AndroidEntryPoint
class ParentProfileFragment: Fragment(R.layout.fragment_parent_profile) {

    private lateinit var binding: FragmentParentProfileBinding
    private lateinit var childrenListAdapter: ChildrenListAdapter
    private val viewModel: ParentProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentParentProfileBinding.bind(view)
        binding.tvParentLogin.text = prefs.login
        binding.pbLoadChildren.visibility = View.VISIBLE

        childrenListAdapter = ChildrenListAdapter(object : ChildrenListActionListener {
            override fun confirmInvitation(child: Child) {
                viewModel.confirmInvitation(child)
            }
        })

        setupRecyclerView()

        viewModel.confirmInvitationLiveData.observe(viewLifecycleOwner){ response ->
            if (response.isSuccessful){
                viewModel.getChildrenInvitations(prefs.id)
            }
            else{
                Toast.makeText(requireContext(), "Ошибка!", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.getChildrenInvitations(prefs.id)
        viewModel.childrenInvitationsLiveData.observe(viewLifecycleOwner){ response ->
            if (response == null) {
                Toast.makeText(requireContext(), "Ошибка подгрузки данных!", Toast.LENGTH_SHORT).show()
            }
            else{
                childrenListAdapter.submitList(response.children)
                binding.pbLoadChildren.visibility = View.INVISIBLE
            }

        }

        binding.btnParentLogout.setOnClickListener {
            prefs.isSignedIn = false
            prefs.login = ""
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