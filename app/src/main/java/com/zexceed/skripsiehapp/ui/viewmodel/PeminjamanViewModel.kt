package com.zexceed.skripsiehapp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zexceed.skripsiehapp.data.model.Peminjaman
import com.zexceed.skripsiehapp.data.model.User
import com.zexceed.skripsiehapp.data.repository.PeminjamanRepository
import com.zexceed.skripsiehapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PeminjamanViewModel @Inject constructor(
    val peminjamanRepository: PeminjamanRepository
) : ViewModel() {

    private val _peminjaman = MutableLiveData<UiState<List<Peminjaman>>>()
    val peminjaman: LiveData<UiState<List<Peminjaman>>>
        get() = _peminjaman

    private val _addPeminjaman = MutableLiveData<UiState<Pair<Peminjaman, String>>>()
    val addPeminjaman: LiveData<UiState<Pair<Peminjaman, String>>>
        get() = _addPeminjaman

    private val _updatePeminjaman = MutableLiveData<UiState<String>>()
    val updatePeminjaman: LiveData<UiState<String>>
        get() = _updatePeminjaman

    private val _deletePeminjaman = MutableLiveData<UiState<String>>()
    val deletePeminjaman: LiveData<UiState<String>>
        get() = _deletePeminjaman

    private val _searchResult = MutableLiveData<UiState<List<Peminjaman>>>()
    val searchResult: LiveData<UiState<List<Peminjaman>>>
        get() = _searchResult


    fun getPeminjaman(user: User?) {
        _peminjaman.value = UiState.Loading
        peminjamanRepository.getPeminjaman { _peminjaman.value = it }
    }

    fun searchPeminjaman(str: String) {
        _searchResult.value = UiState.Loading
        peminjamanRepository.searchPeminjaman(str){_searchResult.value = it}
//    recipeRepository.injectData()
    }


    fun addPeminjaman(peminjaman: Peminjaman) {
        _addPeminjaman.value = UiState.Loading
        peminjamanRepository.addPeminjaman(peminjaman) {
            _addPeminjaman.value = it
        }
    }

    fun updatePeminjaman(peminjaman: Peminjaman) {
        _updatePeminjaman.value = UiState.Loading
        peminjamanRepository.updatePeminjaman(peminjaman) {
            _updatePeminjaman.value = it
        }
    }

    fun deletePeminjaman(peminjaman: Peminjaman) {
        _deletePeminjaman.value = UiState.Loading
        peminjamanRepository.deletePeminjaman(peminjaman) {
            _deletePeminjaman.value = it
        }
    }

    fun onUploadSingleFile(fileUris: Uri, onResult: (UiState<Uri>) -> Unit) {
        onResult.invoke(UiState.Loading)
        viewModelScope.launch {
            peminjamanRepository.uploadSingleFile(fileUris, onResult)
        }
    }

}