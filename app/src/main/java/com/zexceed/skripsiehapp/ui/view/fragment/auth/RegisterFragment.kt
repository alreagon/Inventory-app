package com.zexceed.skripsiehapp.ui.view.fragment.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        binding.tvToLogin.setOnClickListener {
            findNavController().navigate(
                R.id.action_registerFragment_to_loginFragment,
                Bundle().apply {
                    putParcelable("user", objUser)
                })
        }
        binding.btnRegister.setOnClickListener {
            if (validateInput()) {
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                checkEmailExists(email) { emailExists ->
                    if (emailExists) {
                        toast(getString(R.string.invalid_email_already))
                    } else {
                        val user = getUserObj()
                        registerUser(email, password, user)
                    }
                }
            }
        }
    }

    fun observer() {
        binding.apply {

            viewModel.register.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> {
                        btnRegister.setText("")
                        pbRegister.show()
                    }

                    is UiState.Failure -> {
                        btnRegister.setText("Register Error!")
                        pbRegister.hide()
                        toast(state.error)
                    }

                    is UiState.Success -> {
                        btnRegister.setText("Register Successfully")
                        pbRegister.hide()
                        toast(state.data)
                        Handler(Looper.getMainLooper()).postDelayed({
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        }, 1500)
                    }
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        binding.apply {

            var isValid = true

            if (etUkmName.text.isNullOrEmpty()) {
                isValid = false
                toast(getString(R.string.enter_namaUkm))
            }

            if (etEmail.text.isNullOrEmpty()) {
                isValid = false
                toast(getString(R.string.enter_email))
            } else {
                val email = etEmail.text.toString()
                if (!isValidEmail(email)) {
                    isValid = false
                    toast(getString(R.string.invalid_email))
                }
            }

            if (etConPassword.text.isNullOrEmpty()) {
                isValid = false
                toast(getString(R.string.enter_conPass))
            }

            if (etPassword.text.isNullOrEmpty()) {
                isValid = false
                toast(getString(R.string.enter_password))
            } else {
                val password = etPassword.text.toString()
                val confirmPassword = etConPassword.text.toString()
                if (password != confirmPassword) {
                    isValid = false
                    toast(getString(R.string.password_not_match))
                }
                if (password.length < 8) {
                    isValid = false
                    toast(getString(R.string.invalid_password))
                }
            }
            return isValid
        }
    }

    private fun isValidEmail(email: String): Boolean {
        // Validasi format email
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun checkEmailExists(email: String, callback: (Boolean) -> Unit) {
        viewModel.checkEmailExists(email) { exists ->
            callback(exists)
        }
    }

    private fun registerUser(email: String, password: String, user: User) {
        binding.apply {
            viewModel.register(email, password, user)
            viewModel.register.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> {
                        btnRegister.text = ""
                        pbRegister.show()
                    }

                    is UiState.Failure -> {
                        btnRegister.text = "Register Error!"
                        pbRegister.hide()
                        Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                    }

                    is UiState.Success -> {
                        btnRegister.text = "Register Successfully"
                        pbRegister.hide()
                        Toast.makeText(requireContext(), state.data, Toast.LENGTH_SHORT).show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        }, 1500)
                    }
                }
            }
        }
    }

    private fun getUserObj(): User {
        return User(
            id = "",
            namaUkm = binding.etUkmName.text.toString(),
            email = binding.etEmail.text.toString(),
        )
    }

}