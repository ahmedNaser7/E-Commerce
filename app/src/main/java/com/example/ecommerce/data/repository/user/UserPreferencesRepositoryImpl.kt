package com.example.ecommerce.data.repository.user

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.example.ecommerce.data.datasource.datastore.DataStoreKeys.IS_USER_LOGGED_IN
import com.example.ecommerce.data.datasource.datastore.UserPreferencesDataSource
import com.example.ecommerce.data.datasource.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserPreferencesRepositoryImpl(
    private val userPreferencesDataSource: UserPreferencesDataSource
) : UserPreferencesRepository {

    // Read from data source
    override suspend fun isUserLoggedIn(): Flow<Boolean> {
        return userPreferencesDataSource.isUserLoggedIn
    }


    // write to data source
    override suspend fun saveLoginState(isLoggedIn: Boolean) {
        return userPreferencesDataSource.saveLoginState(isLoggedIn)
    }


}