package com.zexceed.skripsiehapp.di

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.zexceed.skripsiehapp.data.repository.AuthRepository
import com.zexceed.skripsiehapp.data.repository.AuthRepositoryImp
import com.zexceed.skripsiehapp.data.repository.InventoryRepository
import com.zexceed.skripsiehapp.data.repository.InventoryRepositoryImp
import com.zexceed.skripsiehapp.data.repository.PeminjamanRepository
import com.zexceed.skripsiehapp.data.repository.PeminjamanRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun providePeminjamanRepository(
        database: FirebaseFirestore,
        storageReference: StorageReference
    ): PeminjamanRepository {
        return PeminjamanRepositoryImp(database,storageReference)
    }

    @Provides
    @Singleton
    fun provideInventoryRepository(
        database: FirebaseFirestore,
        storageReference: StorageReference
    ): InventoryRepository {
        return InventoryRepositoryImp(database,storageReference)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
        appPreferences: SharedPreferences,
        gson: Gson
    ): AuthRepository {
        return AuthRepositoryImp(auth, database, appPreferences, gson)
    }
}