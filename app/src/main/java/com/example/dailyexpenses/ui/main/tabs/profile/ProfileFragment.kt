package com.example.dailyexpenses.ui.main.tabs.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.navOptions
import com.example.dailyexpenses.R
import com.example.dailyexpenses.databinding.FragmentProfileBinding
import com.example.dailyexpenses.utils.findTopNavController
import com.example.dailyexpensespredprof.utils.prefs

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        binding.btnLogout.setOnClickListener {
            prefs.isSignedIn = false
            findTopNavController().navigate(R.id.signInFragment, null, navOptions {
                popUpTo(R.id.tabsFragment){
                    inclusive = true
                }
            })
        }
    }
}