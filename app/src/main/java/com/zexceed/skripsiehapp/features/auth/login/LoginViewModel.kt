package com.zexceed.skripsiehapp.features.auth.login

import androidx.lifecycle.ViewModel
import com.zexceed.skripsiehapp.repository.UserRepository

class LoginViewModel : ViewModel() {
    private val userRepository = UserRepository.getInstance()
    val currentUserLiveData = userRepository.currentUserLiveData

    suspend fun login(email: String, password: String) = userRepository.login(email, password)
}