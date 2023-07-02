package com.zexceed.skripsiehapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Peminjaman(
    var id: String = "",
    val namaBarang: String? = null,
    val kodeBarang: String? = null,
    val namaPeminjam: String? = null,
    val organisasiPeminjam: String? = null,
    val deskripsiBarang: String? = null,
    val kondisiAwal: String? = null,
    val tanggalPeminjaman: String? = null,
    val tanggalPengembalian: String? = null,
    val jumlah: String? = null,
    val jaminan: String? = null,
    val catatan: String? = null,
    val foto: String? = null
) : Parcelable