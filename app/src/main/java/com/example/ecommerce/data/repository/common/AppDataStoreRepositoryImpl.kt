package com.example.ecommerce.data.repository.common

import com.example.ecommerce.data.datasource.datastore.AppPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppDataStoreRepositoryImpl @Inject constructor(
    private val appPreferencesDataSource: AppPreferencesDataSource
) : AppDataStoreRepository {

    // Read from data source
    override suspend fun isUserLoggedIn(): Flow<Boolean> {
        return appPreferencesDataSource.isUserLoggedIn
    }


    // write to data source
    override suspend fun saveLoginState(isLoggedIn: Boolean) {
        return appPreferencesDataSource.saveLoginState(isLoggedIn)
    }
}