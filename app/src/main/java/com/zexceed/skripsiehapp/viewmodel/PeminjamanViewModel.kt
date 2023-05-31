package com.zexceed.skripsiehapp.viewmodel

import androidx.lifecycle.ViewModel
import com.zexceed.skripsiehapp.repository.PeminjamanRepository
import com.zexceed.skripsiehapp.repository.UserRepository

class PeminjamanViewModel : ViewModel() {
    private val peminjamanRepository = PeminjamanRepository.getInstance()
    private val userRepository = UserRepository.getInstance()
    val allPeminjaman = peminjamanRepository.getAllPeminjaman()
    val savedPeminjaman = userRepository.getSavedPeminjaman()
    val savedPeminjamanId = userRepository.getSavedPeminjamanId()
    val currentUserLiveData = userRepository.currentUserLiveData
    val searchResult = peminjamanRepository.searchResult

    suspend fun searchPeminjaman(query: String) {
        peminjamanRepository.searchPeminjaman(query)
//    peminjamanRepository.injectData()
    }

    suspend fun clearSearch() {
        peminjamanRepository.clearSearch()
    }

    suspend fun savePeminjaman(peminjamanId: String)=userRepository.savePeminjaman(peminjamanId)

    suspend fun removePeminjamanFromSaved(peminjamanId: String)=userRepository.removePeminjamanFromSaved(peminjamanId)

    suspend fun logout() = userRepository.logout()
}