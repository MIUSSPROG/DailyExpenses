package com.example.dailyexpenses.ui.main.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dailyexpenses.R
import com.example.dailyexpenses.api.Child
import com.example.dailyexpenses.databinding.FragmentSignInBinding
import com.example.dailyexpensespredprof.utils.prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.log

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private lateinit var binding: FragmentSignInBinding
    private val viewModel: SignInViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignInBinding.bind(view)
        binding.btnSignUpAuth.setOnClickListener { onSignUpButtonPressed() }
        binding.btnSignInAuth.setOnClickListener { onSignInButtonPressed() }

    }

    private fun onSignInButtonPressed(){
        binding.apply {
            val login = etLogin.text.toString()
            val pass = etPassword.text.toString()
            if (login.isNotBlank() && pass.isNotBlank()){
                if (login == prefs.login && pass == prefs.pass){
                    prefs.isSignedIn = true
                    val direction = SignInFragmentDirections.actionSignInFragmentToTabsFragment()
                    findNavController().navigate(direction)
                }
                else{
                    viewModel.checkChild(Child(login = login, password = pass))
                    viewModel.childToCheck.observe(viewLifecycleOwner){
                        it ?: Toast.makeText(requireContext(), "Пользователь не найден", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onSignUpButtonPressed(){
        val direction = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
        findNavController().navigate(direction)
    }

}