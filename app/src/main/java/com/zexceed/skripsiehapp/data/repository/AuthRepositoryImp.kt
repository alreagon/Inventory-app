package com.zexceed.skripsiehapp.data.repository

import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.zexceed.skripsiehapp.data.model.User
import com.zexceed.skripsiehapp.util.FireStoreCollection
import com.zexceed.skripsiehapp.util.SharedPrefConstants
import com.zexceed.skripsiehapp.util.UiState

class AuthRepositoryImp(
    val auth: FirebaseAuth,
    val database: FirebaseFirestore,
    private val appPreferences: SharedPreferences,
    private val gson: Gson
) : AuthRepository {

    //    private val _currentUserLiveData = MutableLiveData<FirebaseUser>()
//    val currentUserLiveData: LiveData<FirebaseUser> = _currentUserLiveData
    override fun registerUser(
        email: String,
        password: String,
        user: User, result: (UiState<String>) -> Unit
    ) {
        val message = MutableLiveData<String>()
        message.postValue("LOADING")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                val currentUser = auth.currentUser
                val userNameNow = user.namaUkm
                val emailNow = user.email
                val profileUpdates = userProfileChangeRequest {
                    displayName = userNameNow
                }
                currentUser!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { mTask ->
                        if (mTask.isSuccessful) {
                            Log.d(TAG, "User profile updated.")
                            val mCurrentUser = auth.currentUser
                            val mahasiswaKos = User(
                                mCurrentUser!!.uid,
                                mCurrentUser.displayName!!,
                                mCurrentUser.email!!
                            )
                            database.collection("user")
                                .document(mahasiswaKos.id)
                                .set(mahasiswaKos)
                                .addOnSuccessListener {
//                                    _currentUserLiveData.postValue(auth.currentUser)
//                                    message.postValue("SUCCESS")
                                }
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", it.exception)
                            message.postValue(it.exception.toString())
                        }
                    }
                    .addOnFailureListener {
                    }
                if (it.isSuccessful) {
                    user.id = it.result.user?.uid ?: ""
                    updateUserInfo(user) { state ->
                        when (state) {
                            is UiState.Success -> {
                                storeSession(id = it.result.user?.uid ?: "") {
                                    if (it == null) {
                                        result.invoke(UiState.Failure("User register successfully but session failed to store"))
                                    } else {
                                        result.invoke(
                                            UiState.Success("User register successfully!")
                                        )
                                    }
                                }
                            }

                            is UiState.Failure -> {
                                result.invoke(UiState.Failure(state.error))
                            }

                            else -> {}
                        }
                    }
                } else {
                    try {
                        throw it.exception ?: java.lang.Exception("Invalid authentication")
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        result.invoke(UiState.Failure("Authentication failed, Password should be at least 6 characters"))
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        result.invoke(UiState.Failure("Authentication failed, Invalid email entered"))
                    } catch (e: FirebaseAuthUserCollisionException) {
                        result.invoke(UiState.Failure("Authentication failed, Email already registered."))
                    } catch (e: Exception) {
                        result.invoke(UiState.Failure(e.message))
                    }
                }
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun updateUserInfo(user: User, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreCollection.USER).document(user.id)
        document
            .set(user)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("User has been update successfully")
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

    override fun loginUser(
        email: String,
        password: String,
        result: (UiState<String>) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storeSession(id = task.result.user?.uid ?: "") {
                        if (it == null) {
                            result.invoke(UiState.Failure("Failed to store local session"))
                        } else {
                            result.invoke(UiState.Success("Login successfully!"))
                        }
                    }
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failure("Authentication failed, Check email and password"))
            }
    }


    override fun forgotPassword(email: String, result: (UiState<String>) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.invoke(UiState.Success("Email has been sent"))

                } else {
                    result.invoke(UiState.Failure(task.exception?.message))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failure("Authentication failed, Check email"))
            }
    }

    override fun logout(result: () -> Unit) {
        auth.signOut()
        appPreferences.edit().putString(SharedPrefConstants.USER_SESSION, null).apply()
        result.invoke()
    }

    override fun storeSession(id: String, result: (User?) -> Unit) {
        database.collection(FireStoreCollection.USER).document(id)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = it.result.toObject(User::class.java)
                    appPreferences.edit()
                        .putString(SharedPrefConstants.USER_SESSION, gson.toJson(user)).apply()
                    result.invoke(user)
                    Log.d(TAG, "success auth storeSession observer utama")
                } else {
                    result.invoke(null)
                }
            }
            .addOnFailureListener {
                result.invoke(null)
            }
    }

    override fun getSession(result: (User?) -> Unit) {
        val user_str = appPreferences.getString(SharedPrefConstants.USER_SESSION, null)
        if (user_str == null) {
            Log.e(TAG, "getSession null")
            result.invoke(null)
        } else {
            val user = gson.fromJson(user_str, User::class.java)
            result.invoke(user)
            Log.d(TAG, "getSession tidak null")
        }
    }

    override fun getUserId(id: String, result: (UiState<User>) -> Unit) {


    }

}