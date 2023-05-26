package com.zexceed.skripsiehapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Inventory(
    val id: String? = null,
    val namaBarang: String? = null,
    val kodeBarang: String? = null,
    val status: String? = null,
    val asalBarang : String? = null,
    val deskripsiBarang : String? = null,
    val catatan: String? = null,
    val foto: String? = null
) : Parcelable