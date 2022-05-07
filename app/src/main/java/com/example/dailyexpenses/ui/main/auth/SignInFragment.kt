package com.example.dailyexpenses.ui.main.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.dailyexpenses.R
import com.example.dailyexpenses.api.Child
import com.example.dailyexpenses.api.Parent
import com.example.dailyexpenses.databinding.FragmentSignInBinding
import com.example.dailyexpenses.ui.main.auth.SignInViewModel.Companion.CHILD_ROLE
import com.example.dailyexpenses.ui.main.auth.SignInViewModel.Companion.PARENT_ROLE
import com.example.dailyexpenses.utils.Hasher
import com.example.dailyexpensespredprof.utils.prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
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
        binding.progressBar.isVisible = true
        binding.apply {
            val login = etLogin.text.toString()
            val pass = etPassword.text.toString()
//            var isParent = true
//            var isChild = true
            val passHash = Hasher.getSecurePassword(pass)
            if (login.isNotBlank() && pass.isNotBlank()) {

                viewModel.checkUser(login, passHash!!)

                viewModel.userToCheck.observe(viewLifecycleOwner){ pair ->
                    if (pair != null){
                        val role = pair.first
                        val id = pair.second
                        saveInfoAndDirect(role, login, id!!)
                    }else{
                        Toast.makeText(requireContext(), "Пользователь не найден", Toast.LENGTH_SHORT).show()
                        binding.progressBar.isVisible = false
                    }
                }
//                viewModel.checkChild(Child(login = login, password = passHash!!))
//                viewModel.checkChildFlow(Child(login = login, password = passHash!!))
//                viewModel.checkParentFlow(Parent(login = login, password = passHash!!))
//                viewModel.checkParent(Parent(login = login, password = passHash!!))

//                lifecycleScope.launchWhenStarted {
//                    viewModel.childToCheckFlow.collect {
//                        when(it){
//                            is SignInViewModel.LoginUiState.Success<*> -> {
//                                saveInfoAndDirect("ученик", login, (it.data as Child).id)
//                            }
//                            is SignInViewModel.LoginUiState.Error -> {
//                                isChild = false
//                            }
//                        }
//                    }
//
//                    viewModel.parentToCheckFlow.collect {
//                        when(it){
//                            is SignInViewModel.LoginUiState.Success<*> -> {
//                                saveInfoAndDirect("родитель", login, (it.data as Parent).id)
//                            }
//                            is SignInViewModel.LoginUiState.Error -> {
//                                isParent = false
//                            }
//                        }
//
//                        if (!isParent && !isChild){
//                            Toast.makeText(requireContext(), "Пользователь не найден", Toast.LENGTH_SHORT).show()
//                            binding.progressBar.isVisible = false
//                        }
//                    }
//
//                }
// MediatorLiveData --------------------------------------------------------
//                viewModel.pairMediatorLiveData.observe(viewLifecycleOwner){
//                    val child = it.first
//                    val parent = it.second
//                    if (child != null){
//                        saveInfoAndDirect("ученик", login, child.id)
//                    }
//                    else if (parent != null){
//                        saveInfoAndDirect("родитель", login, parent.id)
//                    }
//                    else{
//                        Toast.makeText(requireContext(), "Пользователь не найден", Toast.LENGTH_SHORT).show()
//                        progressBar.isVisible = false
//                    }
//                }

//                viewModel.childToCheck.observe(viewLifecycleOwner) { child ->
//                    if (child == null) {
//                        isChild = false
//                        viewModel.parentToCheck.observe(viewLifecycleOwner){ parent ->
//                            if (parent == null) {
//                                isParent = false
//                                Toast.makeText(requireContext(), "Пользователь не найден", Toast.LENGTH_SHORT).show()
//                                binding.progressBar.isVisible = false
//                            }else {
//                                saveInfoAndDirect("родитель", login, parent.id)
//                            }
//                        }
//                    }else {
//                        saveInfoAndDirect("ученик", login, child.id)
//                    }
//                }

//                if (!isParent && !isChild){
//                    Toast.makeText(requireContext(), "Пользователь не найден", Toast.LENGTH_SHORT).show()
//                    binding.progressBar.isVisible = false
//                }

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