package com.zexceed.skripsiehapp.util

sealed class UiState<out T> {
    object Loading: UiState<Nothing>()
    data class Success<out T>(val data: T): UiState<T>()
    data class Failure(val error: String?): UiState<Nothing>()
}
//sealed class Resource<out T>(val data: T? = null, val message: String? = null) {
//    class Success<T>(data: T?) : Resource<T>(data)
//    class Loading<T>(data: T? = null) : Resource<T>(data)
//    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
//}