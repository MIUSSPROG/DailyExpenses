package com.example.dailyexpenses.ui.main.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dailyexpenses.R
import com.example.dailyexpenses.api.ApiResponse
import com.example.dailyexpenses.databinding.FragmentSignInBinding
import com.example.dailyexpenses.ui.main.auth.SignInViewModel.Companion.CHILD_ROLE
import com.example.dailyexpenses.ui.main.auth.SignInViewModel.Companion.PARENT_ROLE
import com.example.dailyexpenses.utils.Hasher
import com.example.dailyexpenses.utils.prefs
import dagger.hilt.android.AndroidEntryPoint

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
        binding.progressBar.isVisible = true
        binding.apply {
            val login = etLogin.text.toString()
            val pass = etPassword.text.toString()
            val passHash = Hasher.getSecurePassword(pass)

            if (login.isNotBlank() && pass.isNotBlank()) {

                viewModel.checkUser(login, passHash!!)

                viewModel.userToCheck.observe(viewLifecycleOwner){
                    when(it){
                        is ApiResponse.Success -> {
                            val role = it.data.first
                            val id = it.data.second

                            viewModel.createUserInDB(id)
                            viewModel.userCreated.observe(viewLifecycleOwner){
                                when(it){
                                    is SignInViewModel.UserCreated.Success<*> -> {
                                        saveInfoAndDirect(role, login, id!!)
                                    }
                                    is SignInViewModel.UserCreated.Error -> {
                                        Toast.makeText(requireContext(), "Произошла ошибка сохранения!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                        is ApiResponse.Error -> {
                            Toast.makeText(requireContext(), "Пользователь не найден!", Toast.LENGTH_SHORT).show()
                            binding.progressBar.isVisible = false
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show()
                progressBar.isVisible = false
            }
        }
    }

    private fun saveInfoAndDirect(role: String, login: String, id: Int){
        binding.progressBar.isVisible = false
        prefs.isSignedIn = true
        prefs.login = login
        prefs.role = role
        prefs.id = id

        val direction = when(role){
            CHILD_ROLE -> SignInFragmentDirections.actionSignInFragmentToTabsFragment()
            PARENT_ROLE -> SignInFragmentDirections.actionSignInFragmentToTabsParentFragment()
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