package com.example.dailyexpenses.ui.main.tabs.parent

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.navOptions
import com.example.dailyexpenses.R
import com.example.dailyexpenses.databinding.FragmentParentProfileBinding
import com.example.dailyexpenses.utils.findTopNavController
import com.example.dailyexpensespredprof.utils.prefs

class ParentProfileFragment: Fragment(R.layout.fragment_parent_profile) {

    private lateinit var binding: FragmentParentProfileBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentParentProfileBinding.bind(view)

        binding.tvParentLogin.text = prefs.login

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
}