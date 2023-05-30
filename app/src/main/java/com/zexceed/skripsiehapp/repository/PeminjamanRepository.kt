package com.zexceed.skripsiehapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.zexceed.skripsiehapp.model.Peminjaman

class PeminjamanRepository {
    private val database = FirebaseFirestore.getInstance()
    private val allPeminjamanLiveData = MutableLiveData<ArrayList<Peminjaman>>()
    private val peminjamanBelow10LiveData = MutableLiveData<ArrayList<Peminjaman>>()
    private val peminjaman10sLiveData = MutableLiveData<ArrayList<Peminjaman>>()
    private val currentPeminjaman = MutableLiveData<Peminjaman>()
    private val _searchResult = MutableLiveData<ArrayList<Peminjaman>>()
    val searchResult: LiveData<ArrayList<Peminjaman>> = _searchResult

    init {
        allPeminjamanLiveData.value = arrayListOf()
        peminjamanBelow10LiveData.value = arrayListOf()
        peminjaman10sLiveData.value = arrayListOf()
        currentPeminjaman.value = Peminjaman()
    }

    fun getPeminjaman(peminjamanId: String): MutableLiveData<Peminjaman> {
        Log.w("PEMINJAMANID", "$peminjamanId")
        database.collection("peminjaman").document(peminjamanId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TEZ", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val mPeminjaman = snapshot.toObject(Peminjaman::class.java)!!
                Log.d("TEZ", "Current data: ${mPeminjaman.namaBarang}")
                currentPeminjaman.postValue(mPeminjaman)
            } else {
                Log.d("TEZ", "Current data: null")
            }
        }

        return currentPeminjaman
    }

    fun getAllPeminjaman(): MutableLiveData<ArrayList<Peminjaman>> {
        database.collection("peminjaman").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TEZ", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val allPeminjaman = arrayListOf<Peminjaman>()
                for (doc in snapshot) {
                    allPeminjaman.add(doc.toObject(Peminjaman::class.java))
                    Log.d("TEZ", "Current data: $doc")
                }
                allPeminjamanLiveData.postValue(allPeminjaman)
            } else {
                Log.d("TEZ", "Current data: null")
            }
        }

        return allPeminjamanLiveData
    }

    suspend fun searchPeminjaman(query: String) {
        if (query == "") {
            _searchResult.postValue(arrayListOf())
        } else {
            database.collection("peminjaman").addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("TEZ", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val allPeminjaman = arrayListOf<Peminjaman>()
                    for (doc in snapshot) {
                        val mPeminjaman = doc.toObject(Peminjaman::class.java)
                        if (mPeminjaman.namaBarang!!.lowercase().contains(query.lowercase())) {
                            allPeminjaman.add(mPeminjaman)
                        }
                        Log.d("TEZ", "Current data: $doc")
                    }
                    _searchResult.postValue(allPeminjaman)

                    Log.d("TEZA", "Current data: ${allPeminjaman}")
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
        private var instance: PeminjamanRepository? = null
        fun getInstance(): PeminjamanRepository {
            return instance ?: synchronized(this) {
                if (instance == null) {
                    instance = PeminjamanRepository()
                }
                return instance as PeminjamanRepository
            }
        }
    }
}