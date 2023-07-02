package com.zexceed.skripsiehapp.data.repository

import com.zexceed.skripsiehapp.data.model.User
import com.zexceed.skripsiehapp.util.UiState

interface AuthRepository {
    fun registerUser(
        email: String,
        password: String,
        user: User,
        result: (UiState<String>) -> Unit
    )

    fun checkEmailExists(email: String, result: (Boolean) -> Unit)
    fun updateUserInfo(user: User, result: (UiState<String>) -> Unit)
    fun loginUser(email: String, password: String, result: (UiState<String>) -> Unit)
    fun forgotPassword(email: String, result: (UiState<String>) -> Unit)
    fun logout(result: () -> Unit)
    fun storeSession(id: String, result: (User?) -> Unit)
    fun getSession(result: (User?) -> Unit)
    fun getUserId(id: String, result: (UiState<User>) -> Unit)
}