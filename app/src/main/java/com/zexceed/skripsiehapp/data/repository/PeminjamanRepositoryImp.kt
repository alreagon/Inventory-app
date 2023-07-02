package com.zexceed.skripsiehapp.data.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.zexceed.skripsiehapp.data.model.Peminjaman
import com.zexceed.skripsiehapp.util.FireStoreCollection
import com.zexceed.skripsiehapp.util.FirebaseStorageConstants.PEMINJAMAN_IMAGES
import com.zexceed.skripsiehapp.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

class PeminjamanRepositoryImp(
    val database: FirebaseFirestore,
    val storageReference: StorageReference
) : PeminjamanRepository {

    private val getImageUrl = MutableLiveData<String>()
    override fun getPeminjaman(result: (UiState<List<Peminjaman>>) -> Unit) {
        database.collection(FireStoreCollection.PEMINJAMAN)
            .get()
            .addOnSuccessListener {
                val mPeminjaman = arrayListOf<Peminjaman>()
                for (document in it) {
                    val peminjaman = document.toObject(Peminjaman::class.java)
                    mPeminjaman.add(peminjaman)
                }
                result.invoke(
                    UiState.Success(mPeminjaman)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun searchPeminjaman(
        query: String,
        result: (UiState<List<Peminjaman>>) -> Unit
    ) {
        val lowercaseQuery = query.toLowerCase(Locale.getDefault())

        database.collection(FireStoreCollection.PEMINJAMAN)
            .addSnapshotListener { snapshot, e ->
                val listPeminjamanResult = mutableListOf<Peminjaman>()
                if (snapshot != null && !snapshot.isEmpty) {
                    snapshot.forEach { mPeminjaman ->
                        val peminjaman = mPeminjaman.toObject(Peminjaman::class.java)
                        val namaBarang = peminjaman.namaBarang?.toLowerCase(Locale.getDefault())
                        if (namaBarang?.contains(lowercaseQuery) == true) {
                            listPeminjamanResult += peminjaman
                        }
                    }
                    result.invoke(UiState.Success(listPeminjamanResult))
                }
            }
    }


//    override fun searchPeminjaman(
//        query: String,
//        result: (UiState<List<Peminjaman>>) -> Unit
//    ) {
//        database.collection(FireStoreCollection.PEMINJAMAN)
//            .addSnapshotListener { snapshot, e ->
//                val listPeminjamanResult = mutableListOf<Peminjaman>()
//                if (snapshot != null && !snapshot.isEmpty) {
//                    snapshot.forEach { mPeminjaman ->
//                        val mPeminjaman = mPeminjaman.toObject(Peminjaman::class.java)
//                        if (mPeminjaman.namaBarang!!.contains(query)) {
//                            listPeminjamanResult += mPeminjaman
//                        }
//                    }
//                    result.invoke(
//                        UiState.Success(
//                            listPeminjamanResult
//                        )
//                    )
//
//                }
//
//            }
//    }


    override fun addPeminjaman(
        peminjaman: Peminjaman,
        result: (UiState<Pair<Peminjaman, String>>) -> Unit
    ) {
        val document = database.collection(FireStoreCollection.PEMINJAMAN).document()
        peminjaman.id = document.id
        document
            .set(peminjaman)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(
                        Pair(
                            peminjaman,
                            "Peminjaman has been created successfully"
                        )
                    )
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun updatePeminjaman(
        peminjaman: Peminjaman,
        result: (UiState<String>) -> Unit
    ) {
        val document =
            database.collection(FireStoreCollection.PEMINJAMAN).document(peminjaman.id)
        document
            .set(peminjaman)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Peminjaman has been update successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun deletePeminjaman(
        peminjaman: Peminjaman,
        result: (UiState<String>) -> Unit
    ) {

        val document =
            database.collection(FireStoreCollection.PEMINJAMAN).document(peminjaman.id)
        document
            .delete()
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Peminjaman has been delete successfully")
                )
            }
            .addOnFailureListener { e ->
                result.invoke(
                    UiState.Failure(
                        e.message
                    )
                )
            }
    }

    override fun getImageUrl(): LiveData<String> {
        return getImageUrl
    }

    fun setImageUrl(url: String) {
        getImageUrl.value = url
    }


    override suspend fun uploadSingleFile(fileUri: Uri, onResult: (UiState<Uri>) -> Unit) {
        try {
            val uri: Uri = withContext(Dispatchers.IO) {
                storageReference.child(PEMINJAMAN_IMAGES)
                    .child("${System.currentTimeMillis()}")
                    .putFile(fileUri)
                    .await()
                    .storage
                    .downloadUrl
                    .await()
            }
            val imageUrl = uri.toString()
            setImageUrl(imageUrl)
            onResult.invoke(UiState.Success(uri))
        } catch (e: FirebaseException) {
            onResult.invoke(UiState.Failure(e.message))
        } catch (e: Exception) {
            onResult.invoke(UiState.Failure(e.message))
        }
    }
}