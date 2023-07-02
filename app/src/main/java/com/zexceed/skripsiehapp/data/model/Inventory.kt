package com.zexceed.skripsiehapp.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Inventory(
    var id: String = "",
    val namaBarang: String? = null,
    val kodeBarang: String? = null,
    val status: String? = null,
    val asalBarang: String? = null,
    val deskripsiBarang: String? = null,
    val catatan: String? = null,
    val foto: String? = null
) : Parcelable