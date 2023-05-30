package com.zexceed.skripsiehapp.repository

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.zexceed.skripsiehapp.model.Inventory
import com.zexceed.skripsiehapp.model.Peminjaman
import com.zexceed.skripsiehapp.model.User
import java.lang.Exception

class UserRepository {
    private val auth = Firebase.auth
    private val database = FirebaseFirestore.getInstance()
    private val _currentUserLiveData = MutableLiveData<FirebaseUser>()
    val currentUserLiveData: LiveData<FirebaseUser> = _currentUserLiveData
    private val savedPeminjaman = MutableLiveData<ArrayList<Peminjaman>>()
    private val savedPeminjamanId = MutableLiveData<ArrayList<String>>()
    private val savedInventory = MutableLiveData<ArrayList<Inventory>>()
    private val savedInventoryId = MutableLiveData<ArrayList<String>>()

    init {
        _currentUserLiveData.value = auth.currentUser
        savedPeminjaman.value = arrayListOf()
        savedInventory.value = arrayListOf()
    }

    suspend fun login(email: String, password: String): MutableLiveData<String> {
        val message = MutableLiveData<String>()
        message.postValue("LOADING")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithEmail:success")
                    _currentUserLiveData.value = auth.currentUser
                    message.postValue("SUCCESS")
                    getSavedPeminjamanId()
                    getSavedPeminjaman()
                    getSavedInventoryId()
                    getSavedInventory()
                } else {
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                    message.postValue(task.exception.toString())
                }
            }
        return message
    }

    suspend fun register(
        email: String,
        namaUkm: String,
        password: String
    ): MutableLiveData<String> {
        val message = MutableLiveData<String>()
        message.postValue("LOADING")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = namaUkm
                    }
                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { mTask ->
                            if (mTask.isSuccessful) {
                                Log.d(ContentValues.TAG, "User profile updated.")
                                val mCurrentUser = auth.currentUser
                                val user = User(
                                    mCurrentUser!!.uid,
                                    mCurrentUser.displayName!!
                                )
                                database.collection("userUkm")
                                    .document(user.id)
                                    .set(user)
                                    .addOnSuccessListener {
                                        _currentUserLiveData.postValue(auth.currentUser)
                                        message.postValue("SUCCESS")
                                    }
                            } else {
                                Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                                message.postValue(task.exception.toString())
                            }
                        }
                        .addOnFailureListener {
                            Log.w(ContentValues.TAG, "updateProfile:failure", task.exception)
                            message.postValue(task.exception.toString())
                        }
                } else {
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    message.postValue(task.exception.toString())
                }
            }
        return message
    }


    //    === Peminjaman ===
    fun getSavedPeminjamanId(): MutableLiveData<ArrayList<String>> {
        val currentUser = auth.currentUser
        currentUser?.let {
            database.collection("userUkm").document(it.uid)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("TEZ", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val allPeminjamanId = arrayListOf<String>()
                        savedPeminjamanId.postValue(allPeminjamanId)
                        val user = snapshot.toObject(User::class.java)
                        Log.d(ContentValues.TAG, "DocumentSnapshot data: ${snapshot.data}")
                        for (i in user!!.listPeminjamanId) {
                            allPeminjamanId.add(i)
                        }
                        Log.d("TEZZ", "Current data: $allPeminjamanId")
                        savedPeminjamanId.postValue(allPeminjamanId)
                    } else {
                        Log.d("TEZ", "Current data: null")
                    }
                }
        }

        return savedPeminjamanId
    }

    fun getSavedPeminjaman(): MutableLiveData<ArrayList<Peminjaman>> {
        val currentUser = auth.currentUser
        currentUser?.let {
            database.collection("userUkm").document(it.uid)
                .addSnapshotListener { snapshot, e ->
                    Log.d("TEZaZ", "Current data: $e")
                    if (e != null) {
                        Log.w("TEZ", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val user = snapshot.toObject(User::class.java)
                        val allPeminjaman = arrayListOf<Peminjaman>()
                        val allPeminjamanId = arrayListOf<String>()
                        savedPeminjaman.postValue(allPeminjaman)
                        Log.d(ContentValues.TAG, "DocumentSnapshot data: ${snapshot.data}")
                        for (i in user!!.listPeminjamanId) {
                            allPeminjamanId.add(i)
                        }
                        Log.d("TEZaZ", "Current data: $allPeminjamanId")
                        database.collection("peminjaman")
                            .get().addOnSuccessListener { mSnapshot ->
                                if (mSnapshot != null && !mSnapshot.isEmpty) {
                                    for (doc in mSnapshot) {
                                        val mPeminjaman = doc.toObject(Peminjaman::class.java)
                                        if (allPeminjamanId.contains(mPeminjaman.id)) {
                                            allPeminjaman.add(mPeminjaman)
                                            Log.d("TEZ", "Current data: $doc")
                                        }
                                    }
                                    savedPeminjaman.postValue(allPeminjaman)
                                } else {
                                    Log.d("TEZ", "Current data: null")
                                }
                            }
                    } else {
                        Log.d("TEZ", "Current data: null")
                    }
                }
        }

        return savedPeminjaman
    }

    suspend fun savePeminjaman(peminjamanId: String): MutableLiveData<String> {
        val currentUser = auth.currentUser
        val statusLiveData = MutableLiveData<String>()
        statusLiveData.postValue("LOADING")
        currentUser?.let {
            database.collection("userUkm").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val user = document.toObject(User::class.java)
                        Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                        user?.listPeminjamanId?.add(peminjamanId)
                        database.collection("userUkm").document(currentUser!!.uid)
                            .set(user!!).addOnSuccessListener {
                                Log.d("TEZ", "Current data: $it")
                                savedPeminjamanId.postValue(user.listPeminjamanId)
                                statusLiveData.postValue("SUCCESS")
                            }.addOnFailureListener {
                                Log.d("TEZ", "Current data: $it")
                                statusLiveData.postValue(it.toString())
                            }
                    } else {
                        Log.d(ContentValues.TAG, "No such document")
                        statusLiveData.postValue("No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "get failed with ", exception)
                    statusLiveData.postValue(exception.toString())
                }
        }
        return statusLiveData
    }

    suspend fun removePeminjamanFromSaved(peminjamanId: String): MutableLiveData<String> {
        val currentUser = auth.currentUser
        val statusLiveData = MutableLiveData<String>()
        statusLiveData.postValue("LOADING")
        currentUser?.let {
            database.collection("userUkm").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val user = document.toObject(User::class.java)
                        Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                        user?.listPeminjamanId?.remove(peminjamanId)
                        database.collection("userUkm").document(currentUser!!.uid)
                            .set(user!!).addOnSuccessListener {
                                Log.d("TEZ", "Current data: $it")
                                savedPeminjamanId.postValue(user.listPeminjamanId)
                                statusLiveData.postValue("SUCCESS")
                            }.addOnFailureListener {
                                Log.d("TEZ", "Current data: $it")
                                statusLiveData.postValue(it.toString())
                            }
                    } else {
                        Log.d(ContentValues.TAG, "No such document")
                        statusLiveData.postValue("No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "get failed with ", exception)
                    statusLiveData.postValue(exception.toString())
                }
        }
        return statusLiveData
    }


    //    === Inventory ===
    fun getSavedInventoryId(): MutableLiveData<ArrayList<String>> {
        val currentUser = auth.currentUser
        currentUser?.let {
            database.collection("userUkm").document(it.uid)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("TEZ", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val allInventoryId = arrayListOf<String>()
                        savedInventoryId.postValue(allInventoryId)
                        val user = snapshot.toObject(User::class.java)
                        Log.d(ContentValues.TAG, "DocumentSnapshot data: ${snapshot.data}")
                        for (i in user!!.listInventoryId) {
                            allInventoryId.add(i)
                        }
                        Log.d("TEZZ", "Current data: $allInventoryId")
                        savedInventoryId.postValue(allInventoryId)
                    } else {
                        Log.d("TEZ", "Current data: null")
                    }
                }
        }

        return savedInventoryId
    }

    fun getSavedInventory(): MutableLiveData<ArrayList<Inventory>> {
        val currentUser = auth.currentUser
        currentUser?.let {
            database.collection("userUkm").document(it.uid)
                .addSnapshotListener { snapshot, e ->
                    Log.d("TEZaZ", "Current data: $e")
                    if (e != null) {
                        Log.w("TEZ", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val user = snapshot.toObject(User::class.java)
                        val allInventory = arrayListOf<Inventory>()
                        val allInventoryId = arrayListOf<String>()
                        savedInventory.postValue(allInventory)
                        Log.d(ContentValues.TAG, "DocumentSnapshot data: ${snapshot.data}")
                        for (i in user!!.listInventoryId) {
                            allInventoryId.add(i)
                        }
                        Log.d("TEZaZ", "Current data: $allInventoryId")
                        database.collection("inventory")
                            .get().addOnSuccessListener { mSnapshot ->
                                if (mSnapshot != null && !mSnapshot.isEmpty) {
                                    for (doc in mSnapshot) {
                                        val mInventory = doc.toObject(Inventory::class.java)
                                        if (allInventoryId.contains(mInventory.id)) {
                                            allInventory.add(mInventory)
                                            Log.d("TEZ", "Current data: $doc")
                                        }
                                    }
                                    savedInventory.postValue(allInventory)
                                } else {
                                    Log.d("TEZ", "Current data: null")
                                }
                            }
                    } else {
                        Log.d("TEZ", "Current data: null")
                    }
                }
        }

        return savedInventory
    }

    suspend fun saveInventory(inventoryId: String): MutableLiveData<String> {
        val currentUser = auth.currentUser
        val statusLiveData = MutableLiveData<String>()
        statusLiveData.postValue("LOADING")
        currentUser?.let {
            database.collection("userUkm").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val user = document.toObject(User::class.java)
                        Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                        user?.listInventoryId?.add(inventoryId)
                        database.collection("userUkm").document(currentUser!!.uid)
                            .set(user!!).addOnSuccessListener {
                                Log.d("TEZ", "Current data: $it")
                                savedInventoryId.postValue(user.listInventoryId)
                                statusLiveData.postValue("SUCCESS")
                            }.addOnFailureListener {
                                Log.d("TEZ", "Current data: $it")
                                statusLiveData.postValue(it.toString())
                            }
                    } else {
                        Log.d(ContentValues.TAG, "No such document")
                        statusLiveData.postValue("No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "get failed with ", exception)
                    statusLiveData.postValue(exception.toString())
                }
        }
        return statusLiveData
    }

    suspend fun removeInventoryFromSaved(inventoryId: String): MutableLiveData<String> {
        val currentUser = auth.currentUser
        val statusLiveData = MutableLiveData<String>()
        statusLiveData.postValue("LOADING")
        currentUser?.let {
            database.collection("userUkm").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val user = document.toObject(User::class.java)
                        Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                        user?.listInventoryId?.remove(inventoryId)
                        database.collection("userUkm").document(currentUser!!.uid)
                            .set(user!!).addOnSuccessListener {
                                Log.d("TEZ", "Current data: $it")
                                savedInventoryId.postValue(user.listInventoryId)
                                statusLiveData.postValue("SUCCESS")
                            }.addOnFailureListener {
                                Log.d("TEZ", "Current data: $it")
                                statusLiveData.postValue(it.toString())
                            }
                    } else {
                        Log.d(ContentValues.TAG, "No such document")
                        statusLiveData.postValue("No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "get failed with ", exception)
                    statusLiveData.postValue(exception.toString())
                }
        }
        return statusLiveData
    }


    //    === Account ===
    suspend fun changeAccount(
        email: String,
        namaUkm: String,
        password: String
    ): MutableLiveData<String> {
        val user = auth.currentUser
        val statusLiveData = MutableLiveData<String>()
        statusLiveData.postValue("LOADING")
        val profileUpdates = userProfileChangeRequest {
            displayName = namaUkm
            displayName = password
        }
        user!!.apply {
            updateEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(ContentValues.TAG, "User updated.")
                        updateProfile(profileUpdates)
                            .addOnCompleteListener { mTask ->
                                if (mTask.isSuccessful) {
                                    Log.d(ContentValues.TAG, "User updated.")
                                    val mCurrentUser = auth.currentUser
                                    val user = User(
                                        mCurrentUser!!.uid,
                                        mCurrentUser.displayName!!
                                    )
                                    statusLiveData.postValue("SUCCESS")
                                    database.collection("userUkm")
                                        .document(user.id)
                                        .update("namaUkm", user.namaUkm)
                                        .addOnSuccessListener {
                                            _currentUserLiveData.postValue(auth.currentUser)
                                        }.addOnFailureListener {
                                            Log.d("TEZZ", "Error : $it")
                                            statusLiveData.postValue("$it")
                                            _currentUserLiveData.postValue(auth.currentUser)
                                        }
                                    database.collection("userUkm")
                                        .document(user.id)
                                        .update("password", user.password)
                                        .addOnSuccessListener {
                                            _currentUserLiveData.postValue(auth.currentUser)
                                        }.addOnFailureListener {
                                            Log.d("TEZZ", "Error : $it")
                                            statusLiveData.postValue("$it")
                                            _currentUserLiveData.postValue(auth.currentUser)
                                        }
                                }
                            }.addOnFailureListener {
                                Log.d("TEZZ", "Error : $it")
                                statusLiveData.postValue("$it")
                                _currentUserLiveData.postValue(auth.currentUser)
                            }
                    }
                }.addOnFailureListener {
                    Log.d("TEZZ", "Error : $it")
                    statusLiveData.postValue("$it")
                    _currentUserLiveData.postValue(auth.currentUser)
                }
        }

        return statusLiveData
    }

    suspend fun logout(): MutableLiveData<String> {
        val message = MutableLiveData<String>()
        message.postValue("LOADING")
        try {
            auth.signOut()
            savedPeminjaman.postValue(arrayListOf())
            savedPeminjamanId.postValue(arrayListOf())
            savedInventory.postValue(arrayListOf())
            savedInventoryId.postValue(arrayListOf())
            _currentUserLiveData.postValue(auth.currentUser)
            message.postValue("SUCCESS")
        } catch (e: Exception) {
            message.postValue(e.toString())
        }
        return message
    }


    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(): UserRepository {
            return instance ?: synchronized(this) {
                if (instance == null) {
                    instance = UserRepository()
                }
                return instance as UserRepository
            }
        }
    }
}