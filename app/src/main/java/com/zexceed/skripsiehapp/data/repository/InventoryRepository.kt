package com.zexceed.skripsiehapp.data.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import com.zexceed.skripsiehapp.data.model.Inventory
import com.zexceed.skripsiehapp.util.UiState

interface InventoryRepository {

    fun getInventory(
        result: (UiState<List<Inventory>>) -> Unit
    )

    fun addInventory(
        inventory: Inventory,
        result: (UiState<Pair<Inventory, String>>) -> Unit
    )

    fun updateInventory(inventory: Inventory, result: (UiState<String>) -> Unit)
    fun deleteInventory(inventory: Inventory, result: (UiState<String>) -> Unit)
    suspend fun uploadSingleFile(fileUri: Uri, onResult: (UiState<Uri>) -> Unit)

    //    suspend fun uploadMultipleFile(fileUri: List<Uri>, onResult: (UiState<List<Uri>>) -> Unit)
    fun getImageUrl(): LiveData<String>
    fun searchInventory(
        query: String,
        result: (UiState<List<Inventory>>) -> Unit
    )
}