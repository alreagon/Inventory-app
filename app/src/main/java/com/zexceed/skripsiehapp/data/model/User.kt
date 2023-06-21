package com.zexceed.skripsiehapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: String = "",
    val namaUkm:  String? = null,
    val email: String = "",
) : Parcelable