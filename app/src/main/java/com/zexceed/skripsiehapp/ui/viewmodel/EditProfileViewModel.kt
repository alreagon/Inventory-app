package com.zexceed.skripsiehapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.zexceed.skripsiehapp.data.repository.EditProfileRepository

class EditProfileViewModel : ViewModel() {

    private val userRepository = EditProfileRepository.getInstance()
    val currentUserLiveData = userRepository.currentUserLiveData

    suspend fun changeProfile(
        email: String,
        name: String,
        currentPasword: String,
        newPassword: String
    ) =
        userRepository.changeProfile(email, name, currentPasword, newPassword)

//    suspend fun changePassword(oldPassword: String, newPassword: String) =
//        userRepository.changePassword(oldPassword, newPassword)
//
//    suspend fun changeEmailAndName(email: String, name: String) =
//        userRepository.changeEmailAndName(email, name)


}