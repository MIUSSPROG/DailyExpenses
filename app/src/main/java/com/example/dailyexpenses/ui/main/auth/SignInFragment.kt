package com.example.dailyexpenses.ui.main.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.dailyexpenses.R
import com.example.dailyexpenses.api.Child
import com.example.dailyexpenses.api.Parent
import com.example.dailyexpenses.databinding.FragmentSignInBinding
import com.example.dailyexpenses.utils.Hasher
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

    private fun onSignInButtonPressed() {

        binding.apply {
            val login = etLogin.text.toString()
            val pass = etPassword.text.toString()
            var isParent = true
            var isChild = true
            val passHash = Hasher.getSecurePassword(pass)
            if (login.isNotBlank() && pass.isNotBlank()) {

                viewModel.checkChild(Child(login = login, password = passHash!!))
                viewModel.checkParent(Parent(login = login, password = passHash!!))

                viewModel.childToCheck.observe(viewLifecycleOwner) { child ->
                    if (child == null) {
                        isChild = false
                    }else {
                        saveInfoAndDirect("ученик", login, child.id)
                    }
                }

                viewModel.parentToCheck.observe(viewLifecycleOwner){ parent ->
                    if (parent == null) {
                        isParent = false
                    }else {
                        saveInfoAndDirect("родитель", login, parent.id)
                    }

                    if (!isParent && !isChild){
                        Toast.makeText(requireContext(), "Пользователь не найден", Toast.LENGTH_SHORT).show()
                    }

                }

            } else {
                Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveInfoAndDirect(role: String, login: String, id: Int){
        prefs.isSignedIn = true
        prefs.login = login
        prefs.role = role
        prefs.id = id

        val direction = when(role){
            "ученик" -> SignInFragmentDirections.actionSignInFragmentToTabsFragment()
            "родитель" -> SignInFragmentDirections.actionSignInFragmentToTabsParentFragment()
            else -> null
        }
        if (direction != null) {
            Toast.makeText(requireContext(), "Успешный вход!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(direction)
        }else{
            Toast.makeText(requireContext(), "Ошибка навигации", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onSignUpButtonPressed() {
        val direction = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
        findNavController().navigate(direction)
    }

}