package com.zexceed.skripsiehapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.zexceed.skripsiehapp.model.Inventory

class InventoryRepository {
    private val database = FirebaseFirestore.getInstance()
    private val allInventoryLiveData = MutableLiveData<ArrayList<Inventory>>()
    private val inventoryBelow10LiveData = MutableLiveData<ArrayList<Inventory>>()
    private val inventory10sLiveData = MutableLiveData<ArrayList<Inventory>>()
    private val currentInventory = MutableLiveData<Inventory>()
    private val _searchResult = MutableLiveData<ArrayList<Inventory>>()
    val searchResult: LiveData<ArrayList<Inventory>> = _searchResult

    init {
        allInventoryLiveData.value = arrayListOf()
        inventoryBelow10LiveData.value = arrayListOf()
        inventory10sLiveData.value = arrayListOf()
        currentInventory.value = Inventory()
    }

    fun getInventory(inventoryId: String): MutableLiveData<Inventory> {
        Log.w("INVENTORYID", "$inventoryId")
        database.collection("inventory").document(inventoryId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TEZ", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val mInventory = snapshot.toObject(Inventory::class.java)!!
                Log.d("TEZ", "Current data: ${mInventory.namaBarang}")
                currentInventory.postValue(mInventory)
            } else {
                Log.d("TEZ", "Current data: null")
            }
        }

        return currentInventory
    }

    fun getAllInventory(): MutableLiveData<ArrayList<Inventory>> {
        database.collection("inventory").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TEZ", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val allInventory = arrayListOf<Inventory>()
                for (doc in snapshot) {
                    allInventory.add(doc.toObject(Inventory::class.java))
                    Log.d("TEZ", "Current data: $doc")
                }
                allInventoryLiveData.postValue(allInventory)
            } else {
                Log.d("TEZ", "Current data: null")
            }
        }

        return allInventoryLiveData
    }

    suspend fun searchInventory(query: String) {
        if (query == "") {
            _searchResult.postValue(arrayListOf())
        } else {
            database.collection("inventory").addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("TEZ", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val allInventory = arrayListOf<Inventory>()
                    for (doc in snapshot) {
                        val mInventory = doc.toObject(Inventory::class.java)
                        if (mInventory.namaBarang!!.lowercase().contains(query.lowercase())) {
                            allInventory.add(mInventory)
                        }
                        Log.d("TEZ", "Current data: $doc")
                    }
                    _searchResult.postValue(allInventory)

                    Log.d("TEZA", "Current data: ${allInventory}")
                } else {
                    Log.d("TEZ", "Current data: null")
                }
            }
        }
    }

    suspend fun clearSearch() {
        _searchResult.postValue(arrayListOf())
    }


    companion object {
        @Volatile
        private var instance: InventoryRepository? = null
        fun getInstance(): InventoryRepository {
            return instance ?: synchronized(this) {
                if (instance == null) {
                    instance = InventoryRepository()
                }
                return instance as InventoryRepository
            }
        }
    }
}