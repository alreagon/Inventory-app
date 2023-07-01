package com.zexceed.skripsiehapp.data.repository

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
import com.zexceed.skripsiehapp.data.model.User

class EditProfileRepository {
    private val auth = Firebase.auth
    private val database = FirebaseFirestore.getInstance()
    private val _currentUserLiveData = MutableLiveData<FirebaseUser>()
    val currentUserLiveData: LiveData<FirebaseUser> = _currentUserLiveData

    init {
        _currentUserLiveData.value = auth.currentUser
    }

    fun changeProfile(
        email: String,
        name: String,
        currentPasssword: String,
        newPassword: String
    ): MutableLiveData<String> {
        val user = auth.currentUser
        val statusLiveData = MutableLiveData<String>()
        statusLiveData.postValue("LOADING")
        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }
        val credential = EmailAuthProvider.getCredential(
            user?.email.toString(),
            currentPasssword
        )

        user!!.apply {
            //change password
            reauthenticate(credential)
                .addOnSuccessListener {
                    user.updatePassword(newPassword).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
//                            _currentUserLiveData.value = auth.currentUser
//                            Log.w(ContentValues.TAG, task.toString())
//                            statusLiveData.postValue("SUCCESS")
                            //update Email
                            updateEmail(email)
                                .addOnCompleteListener { task2 ->
                                    if (task2.isSuccessful) {
                                        Log.d(ContentValues.TAG, "User email address updated.")
                                        updateProfile(profileUpdates)
                                            .addOnCompleteListener { mTask ->
                                                if (mTask.isSuccessful) {
                                                    Log.d(
                                                        ContentValues.TAG,
                                                        "User email address updated."
                                                    )
                                                    val mCurrentUser = auth.currentUser
                                                    val userUkm = User(
                                                        mCurrentUser!!.uid,
                                                        mCurrentUser.displayName!!,
                                                        mCurrentUser.email!!
                                                    )
                                                    statusLiveData.postValue("SUCCESS")
                                                    database.collection("user")
                                                        .document(userUkm.id)
                                                        .update(
                                                            mapOf(
                                                                "namaUkm" to userUkm.namaUkm,
                                                                "email" to userUkm.email
                                                            )
                                                        )
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
                        } else {
                            Log.w(ContentValues.TAG, task.exception)
                            statusLiveData.postValue("$it")
                        }
                    }
                }.addOnFailureListener {
                    Log.w(ContentValues.TAG, it)
                    statusLiveData.postValue("$it")
                }
        }


        return statusLiveData
    }


    companion object {
        @Volatile
        private var instance: EditProfileRepository? = null
        fun getInstance(): EditProfileRepository {
            return instance ?: synchronized(this) {
                if (instance == null) {
                    instance = EditProfileRepository()
                }
                return instance as EditProfileRepository
            }
        }
    }

}