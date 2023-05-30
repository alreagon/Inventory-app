package com.zexceed.skripsiehapp.viewmodel

import androidx.lifecycle.ViewModel
import com.zexceed.skripsiehapp.repository.UserRepository

class RegisterViewModel : ViewModel() {
    private val userRepository = UserRepository.getInstance()
    val currentUserLiveData = userRepository.currentUserLiveData

    suspend fun register(
        email: String,
        namaUkm: String,
        password: String
    ) = userRepository.register(email, namaUkm, password)
}