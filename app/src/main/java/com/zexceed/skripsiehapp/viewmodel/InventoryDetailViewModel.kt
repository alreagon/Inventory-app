package com.zexceed.skripsiehapp.viewmodel

import androidx.lifecycle.ViewModel
import com.zexceed.skripsiehapp.repository.InventoryRepository
import com.zexceed.skripsiehapp.repository.UserRepository

class InventoryDetailViewModel : ViewModel() {

    private val inventoryRepository = InventoryRepository.getInstance()
    private val userRepository = UserRepository.getInstance()
    val savedInventoryId = userRepository.getSavedInventoryId()
    val currentUserLiveData = userRepository.currentUserLiveData


    fun currentInventory(inventoryId: String) = inventoryRepository.getInventory(inventoryId)

    suspend fun saveInventory(inventoryId: String)=userRepository.saveInventory(inventoryId)

    suspend fun removeInventoryFromSaved(inventoryId: String)=userRepository.removeInventoryFromSaved(inventoryId)



}