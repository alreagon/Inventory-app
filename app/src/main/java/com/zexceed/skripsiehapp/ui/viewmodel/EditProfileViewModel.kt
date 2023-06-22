package com.zexceed.skripsiehapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.zexceed.skripsiehapp.data.repository.EditProfileRepository

class EditProfileViewModel : ViewModel() {

    private val userRepository = EditProfileRepository.getInstance()
    val currentUserLiveData = userRepository.currentUserLiveData

    fun changeProfile(
        email: String,
        name: String,
        currentPasword: String,
        newPassword: String
    ) =
        userRepository.changeProfile(email, name, currentPasword, newPassword)

}