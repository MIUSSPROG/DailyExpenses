package com.example.dailyexpenses.ui.main.auth

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dailyexpenses.R
import com.example.dailyexpenses.api.Child
import com.example.dailyexpenses.api.Parent
import com.example.dailyexpenses.databinding.FragmentSignUpBinding
import com.example.dailyexpensespredprof.utils.prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.log

@AndroidEntryPoint
class SignUpFragment: Fragment(R.layout.fragment_sign_up) {

    private lateinit var binding: FragmentSignUpBinding
    private val viewModel: SignUpViewModel by viewModels()
    private lateinit var selectedRole: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignUpBinding.bind(view)
        binding.btnSignUpReg.setOnClickListener { onButtonRegPressed() }
        binding.btnSignInReg.setOnClickListener { onButtonSignInPressed() }


        val roles = listOf("родитель", "ребенок")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, roles)
        binding.dropdownRoles.setAdapter(adapter)

        binding.dropdownRoles.setOnItemClickListener { adapterView, view, position, l ->
//            Toast.makeText(requireContext(), roles[position], Toast.LENGTH_SHORT).show()
            selectedRole = roles[position]
        }
    }

    private fun onButtonRegPressed(){
        binding.apply {
            if (etLoginReg.text.isNotBlank() && etPassReg.text.isNotBlank()){
                val loginToSave = etLoginReg.text.toString()
                val passwordToSave = etPassReg.text.toString()
                prefs.login = loginToSave
                prefs.pass = passwordToSave
                if (selectedRole == "ребенок") {
                    val child = Child(login = loginToSave, password = passwordToSave)
                    viewModel.createChild(child)
                }
                else{
                    val parent = Parent(login = loginToSave, password = passwordToSave)
                    viewModel.createParent(parent)
                }
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