package com.example.dailyexpenses.ui.main.tabs.parent

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.dailyexpenses.R
import com.example.dailyexpenses.databinding.FragmentParentTabsBinding

class TabsParentFragment: Fragment(R.layout.fragment_parent_tabs) {

    private lateinit var binding: FragmentParentTabsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentParentTabsBinding.bind(view)

        val navHost = childFragmentManager.findFragmentById(R.id.tabsParentContainer) as NavHostFragment
        val navController = navHost.navController
        NavigationUI.setupWithNavController(binding.bottomNavigationViewParent, navController)
    }
}