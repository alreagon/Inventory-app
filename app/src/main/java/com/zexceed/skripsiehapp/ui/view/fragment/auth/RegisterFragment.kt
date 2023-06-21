package com.zexceed.skripsiehapp.ui.view.fragment.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.data.model.User
import com.zexceed.skripsiehapp.ui.viewmodel.AuthViewModel
import com.zexceed.skripsiehapp.util.UiState
import com.zexceed.skripsiehapp.util.hide
import com.zexceed.skripsiehapp.util.isValidEmail
import com.zexceed.skripsiehapp.util.show
import com.zexceed.skripsiehapp.util.toast
import com.zexceed.skripsiehapp.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    val TAG: String = "RegisterFragment"
    lateinit var binding: FragmentRegisterBinding
    val viewModel: AuthViewModel by viewModels()
    var objUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        binding.tvToLogin.setOnClickListener {
            findNavController().navigate(
                R.id.action_registerFragment_to_loginFragment,
                Bundle().apply {
                    putParcelable("user", objUser)
                })
        }
        binding.btnRegister.setOnClickListener {
            if (validation()) {
                viewModel.register(
                    email = binding.etEmail.text.toString(),
                    password = binding.etPassword.text.toString(),
                    user = getUserObj()
                )
            }
        }
    }

    fun observer() {
        viewModel.register.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnRegister.setText("")
                    binding.pbRegister.show()
                }

                is UiState.Failure -> {
                    binding.btnRegister.setText("Create Account")
                    binding.pbRegister.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.btnRegister.setText("Register Successfully")
                    binding.pbRegister.hide()
                    toast(state.data)
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().navigate(R.id.action_registerFragment_to_homeNavigation)
                    }, 1500)
                }
            }
        }
    }

    fun getUserObj(): User {
        return User(
            id = "",
            namaUkm = binding.etUkmName.text.toString(),
            email = binding.etEmail.text.toString(),
        )
    }

    fun validation(): Boolean {
        var isValid = true

        if (binding.etUkmName.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_namaUkm))
        }
        if (binding.etEmail.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_email))
        } else {
            if (!binding.etEmail.text.toString().isValidEmail()) {
                isValid = false
                toast(getString(R.string.invalid_email))
            }
        }
        if (binding.etConPassword.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_conPass))
        }
        if (binding.etPassword.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_password))
        } else {
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConPassword.text.toString()
            if (password != confirmPassword) {
                isValid = false
                toast(getString(R.string.password_not_match))
            }
            if (binding.etPassword.text.toString().length < 8) {
                isValid = false
                toast(getString(R.string.invalid_password))
            }
        }
        return isValid
    }

}