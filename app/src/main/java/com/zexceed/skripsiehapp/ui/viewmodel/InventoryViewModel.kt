package com.zexceed.skripsiehapp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zexceed.skripsiehapp.data.model.Inventory
import com.zexceed.skripsiehapp.data.repository.InventoryRepository
import com.zexceed.skripsiehapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    val inventoryRepository: InventoryRepository
) : ViewModel() {

    private val _inventory = MutableLiveData<UiState<List<Inventory>>>()
    val inventory: LiveData<UiState<List<Inventory>>>
        get() = _inventory

    private val _addInventory = MutableLiveData<UiState<Pair<Inventory, String>>>()
    val addInventory: LiveData<UiState<Pair<Inventory, String>>>
        get() = _addInventory

    private val _updateInventory = MutableLiveData<UiState<String>>()
    val updateInventory: LiveData<UiState<String>>
        get() = _updateInventory

    private val _deleteInventory = MutableLiveData<UiState<String>>()
    val deleteInventory: LiveData<UiState<String>>
        get() = _deleteInventory

    private val _searchResult = MutableLiveData<UiState<List<Inventory>>>()
    val searchResult: LiveData<UiState<List<Inventory>>>
        get() = _searchResult


    val getImageUrl: LiveData<String> = inventoryRepository.getImageUrl()

    fun getInventory() {
        _inventory.value = UiState.Loading
        inventoryRepository.getInventory { _inventory.value = it }
    }

    fun searchInventory(str: String) {
        _searchResult.value = UiState.Loading
        inventoryRepository.searchInventory(str) { _searchResult.value = it }
    }


    fun addInventory(inventory: Inventory) {
        _addInventory.value = UiState.Loading
        inventoryRepository.addInventory(inventory) {
            _addInventory.value = it
        }
    }

    fun updateInventory(inventory: Inventory) {
        _updateInventory.value = UiState.Loading
        inventoryRepository.updateInventory(inventory) {
            _updateInventory.value = it
        }
    }

    fun deleteInventory(inventory: Inventory) {
        _deleteInventory.value = UiState.Loading
        inventoryRepository.deleteInventory(inventory) {
            _deleteInventory.value = it
        }
    }

    fun onUploadSingleFile(fileUris: Uri, onResult: (UiState<Uri>) -> Unit) {
        onResult.invoke(UiState.Loading)
        viewModelScope.launch {
            inventoryRepository.uploadSingleFile(fileUris, onResult)
        }
    }

}