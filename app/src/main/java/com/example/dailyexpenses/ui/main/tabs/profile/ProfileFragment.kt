package com.example.dailyexpenses.ui.main.tabs.profile

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navOptions
import com.example.dailyexpenses.R
import com.example.dailyexpenses.api.ChildParent
import com.example.dailyexpenses.api.Parent
import com.example.dailyexpenses.databinding.FragmentProfileBinding
import com.example.dailyexpenses.utils.UiState
import com.example.dailyexpenses.utils.findTopNavController
import com.example.dailyexpenses.utils.prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private var selectedParent: Parent? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        binding.tvLogin.text = prefs.login

        viewModel.checkChildParent(prefs.login)
        viewModel.checkChildParentLiveData.observe(viewLifecycleOwner){ response ->
            when(response){
                is UiState.Success -> {
                    if (response.data!!.confirmed) pinParent(response.data!!) else unpinParent()
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), response.exception.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.checkChildParentChannelFlow.collect {
                    when(it){
                        is UiState.Success -> {
                            unpinParent()
                            Toast.makeText(requireContext(), "Родитель откреплен!", Toast.LENGTH_SHORT).show()
                        }
                        is UiState.Error -> {
                            Toast.makeText(requireContext(), "Ошибка открепления!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }


        viewModel.getParents()
        viewModel.parentsLiveData.observe(viewLifecycleOwner){ response ->
            when(response){
                is UiState.Success -> {
                    var adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, response.data!!)
                    binding.autoEtParentUsername.apply {
                        setAdapter(adapter)
                        threshold = 2
                        setOnItemClickListener { adapterView, view, pos, id ->
                            selectedParent = adapter.getItem(pos)
                        }
                    }
                }
            }
        }

        binding.btnSendInvitationToParent.setOnClickListener {
            binding.autoEtParentUsername.setText("")

            if (selectedParent == null){
                val parentLoginToCheck = binding.autoEtParentUsername.text.toString()
                viewModel.checkParent(parentLoginToCheck)
            }else{
                viewModel.checkInvitation(selectedParent!!)
            }

            viewModel.checkInvitationLiveData.observe(viewLifecycleOwner){ response ->
                when(response){
                    is UiState.Success -> {
                        Toast.makeText(requireContext(), "Приглашение $selectedParent уже было отправлено", Toast.LENGTH_SHORT).show()
                    }
                    is UiState.Error -> {
                        viewModel.sendInvitation(selectedParent!!)
                    }
                }
            }

            viewModel.checkParentLiveData.observe(viewLifecycleOwner){ response ->
                when(response){
                    is UiState.Success -> {
                        if (response.data == null){
                            Toast.makeText(requireContext(), "Такого родителя не существует", Toast.LENGTH_SHORT).show()
                        }else {
                            viewModel.checkInvitation(response.data)
                        }
                    }
                    is UiState.Error -> {
                        Toast.makeText(requireContext(), "Ошибка!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            viewModel.fcmLiveData.observe(viewLifecycleOwner){
                when(it){
                    is UiState.Success -> {
                        Toast.makeText(requireContext(), "Приглашение успешно отправлено!", Toast.LENGTH_SHORT).show()
                    }
                    is UiState.Error -> {
                        Toast.makeText(requireContext(), "Произошла ошибка отправки! ${it.exception.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnLogout.setOnClickListener {
            resetPrefs()
            findTopNavController().navigate(R.id.signInFragment, null, navOptions {
                popUpTo(R.id.tabsFragment){
                    inclusive = true
                }
            })
        }
    }

    private fun pinParent(childParent: ChildParent){
        binding.apply {
            tvParentAttached.text = childParent.parentLogin
            attachedParentConstraintLayout.visibility = View.VISIBLE
            seachParentConstraintLayout.visibility = View.GONE
            btnUnpinParent.setOnClickListener {
                viewModel.cancelInvitation()
            }
        }
    }

    private fun unpinParent(){
        binding.apply {
            attachedParentConstraintLayout.visibility = View.GONE
            seachParentConstraintLayout.visibility = View.VISIBLE
        }
    }

    private fun resetPrefs(){
        prefs.isSignedIn = false
        prefs.login = ""
        prefs.role = ""
        prefs.id = 0
    }
}