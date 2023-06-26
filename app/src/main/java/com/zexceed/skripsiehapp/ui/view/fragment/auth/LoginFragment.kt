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
import com.zexceed.skripsiehapp.databinding.FragmentLoginBinding
import com.zexceed.skripsiehapp.ui.viewmodel.AuthViewModel
import com.zexceed.skripsiehapp.util.UiState
import com.zexceed.skripsiehapp.util.hide
import com.zexceed.skripsiehapp.util.isValidEmail
import com.zexceed.skripsiehapp.util.show
import com.zexceed.skripsiehapp.util.toast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment() {

    val TAG: String = "LoginFragment"
    lateinit var binding: FragmentLoginBinding
    val viewModel: AuthViewModel by viewModels()
    var objUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        binding.btnLogin.setOnClickListener {
            if (validation()) {
                viewModel.login(
                    email = binding.etEmail.text.toString(),
                    password = binding.etPassword.text.toString()
                )
            }
        }
        binding.tvForgotPass.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

        binding.tvToregister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    fun observer() {
        viewModel.login.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnLogin.setText("")
                    binding.pbLogin.show()
                }

                is UiState.Failure -> {
                    binding.btnLogin.setText("Login error!")
                    binding.pbLogin.hide()
                    toast(state.error)
                }

                is UiState.Success -> {
                    binding.btnLogin.setText("Login successfully")
                    binding.pbLogin.hide()
                    toast(state.data)
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().navigate(
                            R.id.action_loginFragment_to_homeNavigation,
                            Bundle().apply {
                                putParcelable("user", objUser)
                            })
                    }, 1500)
                }
            }
        }
    }

    fun validation(): Boolean {
        var isValid = true

        if (binding.etEmail.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_email))
        } else {
            if (!binding.etEmail.text.toString().isValidEmail()) {
                isValid = false
                toast(getString(R.string.invalid_email))
            }
        }
        if (binding.etPassword.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_password))
        } else {
            if (binding.etPassword.text.toString().length < 8) {
                isValid = false
                toast(getString(R.string.invalid_password))
            }
        }
        return isValid
    }

    override fun onStart() {
        super.onStart()
        viewModel.getSession { user ->
            if (user != null) {
                findNavController().navigate(
                    R.id.action_loginFragment_to_homeNavigation,
                    Bundle().apply {
                        putParcelable("user", objUser)
                    })
            }
        }
    }

}

