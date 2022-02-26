package com.example.dailyexpenses.ui.start

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.dailyexpenses.R
import com.example.dailyexpenses.databinding.FragmentStartBinding
import com.example.dailyexpenses.ui.main.MainActivity
import com.example.dailyexpenses.ui.main.MainActivityArgs

class StartFragment : Fragment(R.layout.fragment_start) {

    private lateinit var binding: FragmentStartBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStartBinding.bind(view)

        binding.btnStart.setOnClickListener {
            launchMainScreen(false)
        }
    }

    private fun launchMainScreen(isSignedIn: Boolean){
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val args = MainActivityArgs(isSignedIn)
        intent.putExtras(args.toBundle())
        startActivity(intent)
    }

}