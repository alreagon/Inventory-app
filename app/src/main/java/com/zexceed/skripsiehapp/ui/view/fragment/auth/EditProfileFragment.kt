package com.zexceed.skripsiehapp.ui.view.fragment.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.zexceed.skripsiehapp.R
import com.zexceed.skripsiehapp.data.model.User
import com.zexceed.skripsiehapp.ui.viewmodel.AuthViewModel
import com.zexceed.skripsiehapp.ui.viewmodel.EditProfileViewModel
import com.zexceed.skripsiehapp.util.hide
import com.zexceed.skripsiehapp.util.isValidEmail
import com.zexceed.skripsiehapp.util.show
import com.zexceed.skripsiehapp.util.toast
import com.zexceed.skripsiehapp.databinding.FragmentEditProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment() {
    val TAG: String = "EditProfileFragment"
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val editProfileViewModel: EditProfileViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    var objUser: User? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            objUser = arguments?.getParcelable("user")
            objUser?.let { data ->
                etUkmName.setText(data.namaUkm)
                tvNamaUkm.setText(data.namaUkm)
            } ?: run {
                etUkmName.setText("")
                tvNamaUkm.setText("")
            }
            val currentUser = editProfileViewModel.currentUserLiveData.value
//            etUkmName.setText(currentUser?.displayName)
//            tvNamaUkm.setText(currentUser?.displayName)
            etEmail.setText(currentUser?.email)
            btnSave.setOnClickListener {
                val name = etUkmName.text.toString()
                val email = etEmail.text.toString()
                val currentPassword = etCurrentPassword.text.toString()
                val newPassword = etNewPassword.text.toString()
                val confirmNewPassword = etConfirmNewPassword.text.toString()
                if (name == "" || email == "" || currentPassword == "" || newPassword == "" || confirmNewPassword == "") {
                    toast("Lengkapi data")
                } else {
                    if (!email.isValidEmail()) {
                        toast("Email tidak benar")
                    } else if (newPassword != confirmNewPassword) {
                        toast("Password and confirm password do not match")
                    } else if (binding.etCurrentPassword.text.toString().length < 8) {
                        toast(getString(R.string.invalid_current_password))
                    } else if (binding.etNewPassword.text.toString().length < 8) {
                        toast(getString(R.string.invalid_new_password))
                    } else if (binding.etConfirmNewPassword.text.toString().length < 8) {
                        toast(getString(R.string.invalid_confirm_new_password))
                    } else {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val message = editProfileViewModel.changeProfile(
                                email,
                                name,
                                currentPassword,
                                newPassword
                            )
                            lifecycleScope.launch(Dispatchers.Main) {
                                message.observe(viewLifecycleOwner) {
                                    when (it) {
                                        "SUCCESS" -> {
                                            toast("Update data success")
                                            btnSave.setText("Saved!")
                                            progressBar.hide()
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment2)
                                            }, 1500)
                                        }

                                        "LOADING" -> {
                                            btnSave.setText("Saved!")
                                            progressBar.show()

                                        }

                                        else -> {
                                            toast("Update data success")
                                            btnSave.setText("Saved!")
                                            progressBar.show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            icBackEditProfile.setOnClickListener {
                findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment2)
            }
        }
    }
}