package com.zexceed.skripsiehapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val namaUkm: String = "",
    val email: String = "",
    val password : String = "",
    val listPeminjamanId: ArrayList<String> = arrayListOf(),
    val listInventoryId: ArrayList<String> = arrayListOf()
) : Parcelable