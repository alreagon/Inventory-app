package com.zexceed.skripsiehapp.viewmodel

import androidx.lifecycle.ViewModel
import com.zexceed.skripsiehapp.repository.InventoryRepository
import com.zexceed.skripsiehapp.repository.UserRepository

class InventoryViewModel : ViewModel() {
    private val inventoryRepository = InventoryRepository.getInstance()
    private val userRepository = UserRepository.getInstance()
    val allInventory = inventoryRepository.getAllInventory()
    val savedInventory = userRepository.getSavedInventory()
    val savedInventoryId = userRepository.getSavedInventoryId()
    val currentUserLiveData = userRepository.currentUserLiveData
    val searchResult = inventoryRepository.searchResult

    suspend fun searchInventory(query: String) {
        inventoryRepository.searchInventory(query)
//    inventoryRepository.injectData()
    }

    suspend fun clearSearch() {
        inventoryRepository.clearSearch()
    }

    suspend fun saveInventory(inventoryId: String)=userRepository.saveInventory(inventoryId)

    suspend fun removeInventoryFromSaved(inventoryId: String)=userRepository.removeInventoryFromSaved(inventoryId)

    suspend fun logout() = userRepository.logout()
}