package com.zexceed.skripsiehapp.data.repository

import android.net.Uri
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

class PeminjamanRepositoryImp(
    val database: FirebaseFirestore,
    val storageReference: StorageReference
) : PeminjamanRepository {

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
        database.collection(FireStoreCollection.PEMINJAMAN)
            .addSnapshotListener { snapshot, e ->
                val listPeminjamanResult = mutableListOf<Peminjaman>()
                if (snapshot != null && !snapshot.isEmpty) {
                    snapshot.forEach { mPeminjaman ->
                        val mPeminjaman = mPeminjaman.toObject(Peminjaman::class.java)
                        if (mPeminjaman.namaBarang!!.contains(query)) {
                            listPeminjamanResult += mPeminjaman
                        }
                    }
                    result.invoke(
                        UiState.Success(
                            listPeminjamanResult
                        )
                    )

                }

            }
    }


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

    override suspend fun uploadSingleFile(fileUri: Uri, onResult: (UiState<Uri>) -> Unit) {
        try {
            val uri: Uri = withContext(Dispatchers.IO) {
                storageReference
                    .putFile(fileUri)
                    .await()
                    .storage
                    .downloadUrl
                    .await()
            }
            onResult.invoke(UiState.Success(uri))
        } catch (e: FirebaseException) {
            onResult.invoke(UiState.Failure(e.message))
        } catch (e: Exception) {
            onResult.invoke(UiState.Failure(e.message))
        }
    }

    override suspend fun uploadMultipleFile(
        fileUri: List<Uri>,
        onResult: (UiState<List<Uri>>) -> Unit
    ) {
        try {
            val uri: List<Uri> = withContext(Dispatchers.IO) {
                fileUri.map { image ->
                    async {
                        storageReference.child(PEMINJAMAN_IMAGES)
                            .child(image.lastPathSegment ?: "${System.currentTimeMillis()}")
                            .putFile(image)
                            .await()
                            .storage
                            .downloadUrl
                            .await()
                    }
                }.awaitAll()
            }
            onResult.invoke(UiState.Success(uri))
        } catch (e: FirebaseException) {
            onResult.invoke(UiState.Failure(e.message))
        } catch (e: Exception) {
            onResult.invoke(UiState.Failure(e.message))
        }
    }

}