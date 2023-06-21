package com.zexceed.skripsiehapp.data.repository

import android.net.Uri
import com.zexceed.skripsiehapp.data.model.Peminjaman
import com.zexceed.skripsiehapp.util.UiState

interface PeminjamanRepository {

    fun getPeminjaman(
        result: (UiState<List<Peminjaman>>) -> Unit
    )

    fun addPeminjaman(
        peminjaman: Peminjaman,
        result: (UiState<Pair<Peminjaman, String>>) -> Unit
    )

    fun updatePeminjaman(peminjaman: Peminjaman, result: (UiState<String>) -> Unit)
    fun deletePeminjaman(peminjaman: Peminjaman, result: (UiState<String>) -> Unit)
    suspend fun uploadSingleFile(fileUri: Uri, onResult: (UiState<Uri>) -> Unit)
    suspend fun uploadMultipleFile(fileUri: List<Uri>, onResult: (UiState<List<Uri>>) -> Unit)
    fun searchPeminjaman(
        query: String,
        result: (UiState<List<Peminjaman>>) -> Unit
    )
}