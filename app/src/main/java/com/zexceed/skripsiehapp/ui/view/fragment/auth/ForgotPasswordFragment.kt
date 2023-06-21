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
import com.zexceed.skripsiehapp.ui.viewmodel.AuthViewModel
import com.zexceed.skripsiehapp.util.UiState
import com.zexceed.skripsiehapp.util.hide
import com.zexceed.skripsiehapp.util.isValidEmail
import com.zexceed.skripsiehapp.util.show
import com.zexceed.skripsiehapp.util.toast
import com.zexceed.skripsiehapp.databinding.FragmentForgotPasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    val TAG: String = "ForgotPasswordFragment"
    lateinit var binding: FragmentForgotPasswordBinding
    val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        binding.btnForgotPass.setOnClickListener {
            if (validation()){
                viewModel.forgotPassword(binding.etEmailForgot.text.toString())
            }
        }
    }

    private fun observer(){
        viewModel.forgotPassword.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.btnForgotPass.setText("")
                    binding.pbForgotPass.show()
                }
                is UiState.Failure -> {
                    binding.btnForgotPass.setText("Send")
                    binding.pbForgotPass.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.btnForgotPass.setText("Send")
                    binding.pbForgotPass.hide()
                    toast(state.data)
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().navigateUp()
                    }, 1500)
                }
            }
        }
    }

    fun validation(): Boolean {
        var isValid = true

        if (binding.etEmailForgot.text.isNullOrEmpty()){
            isValid = false
            toast(getString(R.string.enter_email))
        }else{
            if (!binding.etEmailForgot.text.toString().isValidEmail()){
                isValid = false
                toast(getString(R.string.invalid_email))
            }
        }

        return isValid
    }


}