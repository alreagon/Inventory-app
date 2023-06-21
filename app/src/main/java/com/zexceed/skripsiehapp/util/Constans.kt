package com.zexceed.skripsiehapp.util

object FireStoreCollection {
    val PEMINJAMAN = "peminjaman"
    val USER = "user"
    val INVENTORY = "inventory"
}

object FireStoreDocumentField {
    val DATE = "date"
    val USER_ID = "user_id"
}


object SharedPrefConstants {
    val LOCAL_SHARED_PREF = "local_shared_pref"
    val USER_SESSION = "user_session"
}

object FirebaseStorageConstants {
    val ROOT_DIRECTORY = "images"
    val PEMINJAMAN_IMAGES = "peminjaman"
    val INVENTORY_IMAGES = "inventory"
}

enum class HomeTabs(val index: Int, val key: String) {
    PEMINJAMAN(0, "peminjaman"),
    INVENTORY(1, "inventory"),
}
