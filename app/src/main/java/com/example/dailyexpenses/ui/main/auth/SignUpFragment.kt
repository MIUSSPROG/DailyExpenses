package com.example.dailyexpenses.ui.main.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dailyexpenses.R
import com.example.dailyexpenses.databinding.FragmentSignUpBinding
import com.example.dailyexpensespredprof.utils.prefs

class SignUpFragment: Fragment(R.layout.fragment_sign_up) {

    private lateinit var binding: FragmentSignUpBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignUpBinding.bind(view)
        binding.btnSignUpReg.setOnClickListener { onButtonRegPressed() }
        binding.btnSignInReg.setOnClickListener { onButtonSignInPressed() }
    }

    private fun onButtonRegPressed(){
        binding.apply {
            if (etLoginReg.text.isNotBlank() && etPassReg.text.isNotBlank()){
                prefs.login = etLoginReg.text.toString()
                prefs.pass = etPassReg.text.toString()
                Toast.makeText(requireContext(), "Вы успешно зарегистрированы!", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onButtonSignInPressed(){
        findNavController().popBackStack()
    }

}