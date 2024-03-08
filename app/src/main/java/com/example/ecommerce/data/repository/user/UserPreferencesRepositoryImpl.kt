package com.example.ecommerce.data.repository.user

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.example.ecommerce.data.datastore.DataStoreKeys.IS_USER_LOGGED_IN
import com.example.ecommerce.data.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepositoryImpl(private val context: Context):UserPreferencesRepository {

    // Read from Data Store
    override suspend fun isUserLoggedIn(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[IS_USER_LOGGED_IN] ?: false
        }
    }

    // write to Data Store
    override suspend fun saveLoginState(isLoggedIn: Boolean) {
        context.dataStore.edit{ preferences->
            preferences[IS_USER_LOGGED_IN]=isLoggedIn
        }
    }



}