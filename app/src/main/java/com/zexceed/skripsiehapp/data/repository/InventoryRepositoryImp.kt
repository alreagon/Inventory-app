package com.zexceed.skripsiehapp.data.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.zexceed.skripsiehapp.data.model.Inventory
import com.zexceed.skripsiehapp.util.FireStoreCollection
import com.zexceed.skripsiehapp.util.FirebaseStorageConstants.INVENTORY_IMAGES
import com.zexceed.skripsiehapp.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class InventoryRepositoryImp(
    val database: FirebaseFirestore,
    val storageReference: StorageReference
) : InventoryRepository {

    private val getImageUrl = MutableLiveData<String>()
    override fun getInventory(result: (UiState<List<Inventory>>) -> Unit) {
        database.collection(FireStoreCollection.INVENTORY)
            .get()
            .addOnSuccessListener {
                val mInventory = arrayListOf<Inventory>()
                for (document in it) {
                    val inventory = document.toObject(Inventory::class.java)
                    mInventory.add(inventory)
                }
                result.invoke(
                    UiState.Success(mInventory)
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

    override fun searchInventory(
        query: String,
        result: (UiState<List<Inventory>>) -> Unit
    ) {
        database.collection(FireStoreCollection.INVENTORY)
            .addSnapshotListener { snapshot, e ->
                val listInventoryResult = mutableListOf<Inventory>()
                if (snapshot != null && !snapshot.isEmpty) {
                    snapshot.forEach { mInventory ->
                        val mInventory = mInventory.toObject(Inventory::class.java)
                        if (mInventory.namaBarang!!.contains(query)) {
                            listInventoryResult += mInventory
                        }
                    }
                    result.invoke(
                        UiState.Success(
                            listInventoryResult
                        )
                    )

                }

            }
    }


    override fun addInventory(
        inventory: Inventory,
        result: (UiState<Pair<Inventory, String>>) -> Unit
    ) {
        val document = database.collection(FireStoreCollection.INVENTORY).document()
        inventory.id = document.id
        document
            .set(inventory)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(
                        Pair(
                            inventory,
                            "Inventory has been created successfully"
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

    override fun updateInventory(
        inventory: Inventory,
        result: (UiState<String>) -> Unit
    ) {
        val document =
            database.collection(FireStoreCollection.INVENTORY).document(inventory.id)
        document
            .set(inventory)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Inventory has been update successfully")
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

    override fun deleteInventory(
        inventory: Inventory,
        result: (UiState<String>) -> Unit
    ) {

        val document =
            database.collection(FireStoreCollection.INVENTORY).document(inventory.id)
        document
            .delete()
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Inventory has been delete successfully")
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
                storageReference.child(INVENTORY_IMAGES)
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

//    override suspend fun uploadSingleFile(fileUri: Uri, onResult: (UiState<Uri>) -> Unit) {
//        try {
//            val filePath =
//                storageReference.child(INVENTORY_IMAGES).child("${System.currentTimeMillis()}")
////            val uploadTask = filePath.putFile(Uri.parse(fileUri.toString())).await()
//            val uploadTask = filePath.putFile(fileUri)
//            val downloadUrl = filePath.downloadUrl.await()
//            uploadTask.continueWithTask { getDownloadUrl ->
//                if (!getDownloadUrl.isSuccessful) {
//                    getDownloadUrl.exception?.let {
//                        throw it
//                    }
//                }
//                filePath.downloadUrl
//
//            }.addOnCompleteListener { getDownloadTaskStatus ->
//                if (getDownloadTaskStatus.isSuccessful) {
//                    getImageUrl.postValue(getDownloadTaskStatus.result.toString())
//                    onResult.invoke(UiState.Success(downloadUrl))
//                } else {
//                    getImageUrl.postValue("")
//                }
//            }
//
//        } catch (e: Exception) {
//            onResult.invoke(UiState.Failure(e.message))
//        } catch (e: FirebaseException) {
//            onResult.invoke(UiState.Failure(e.message))
//        }
//
//    }
}