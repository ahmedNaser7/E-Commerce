package com.example.ecommerce.di

import com.example.ecommerce.data.datasource.datastore.AppPreferencesDataSource
import com.example.ecommerce.data.repository.auth.FirebaseAuthRepository
import com.example.ecommerce.data.repository.auth.FirebaseAuthRepositoryImpl
import com.example.ecommerce.data.repository.common.AppDataStoreRepository
import com.example.ecommerce.data.repository.common.AppDataStoreRepositoryImpl
import com.example.ecommerce.data.repository.user.UserFireStoreRepository
import com.example.ecommerce.data.repository.user.UserFireStoreRepositoryImpl
import com.example.ecommerce.data.repository.user.UserPreferencesRepository
import com.example.ecommerce.data.repository.user.UserPreferencesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFirebaseAuthRepository(
        firebaseAuthRepositoryImpl: FirebaseAuthRepositoryImpl
    ): FirebaseAuthRepository

    @Binds
    @Singleton
    abstract fun bindAppDatastoreRepository(
        appDataStoreRepositoryImpl: AppDataStoreRepositoryImpl
    ): AppDataStoreRepository

    @Binds
    @Singleton
    abstract fun bindUserFireStoreRepository(
        userFireStoreRepositoryImpl: UserFireStoreRepositoryImpl
    ): UserFireStoreRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        userPreferencesRepositoryImpl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository
}