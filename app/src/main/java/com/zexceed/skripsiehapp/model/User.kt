package com.zexceed.skripsiehapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val namaUkm: String = "",
    val listPeminjmaanId: ArrayList<String> = arrayListOf(),
    val listInventoryId: ArrayList<String> = arrayListOf()
) : Parcelable