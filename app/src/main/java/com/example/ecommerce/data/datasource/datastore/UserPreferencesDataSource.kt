package com.example.ecommerce.data.datasource.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class UserPreferencesDataSource(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    // write
    suspend fun saveLoginState(isLoggedIn:Boolean){
        context.dataStore.edit {
            it[DataStoreKeys.IS_USER_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun saveUserId(userId:String){
        context.dataStore.edit {
            it[DataStoreKeys.USER_ID]=userId
        }
    }

   // read
    val isUserLoggedIn :Flow<Boolean> = context.dataStore.data
        .map {
        it[DataStoreKeys.IS_USER_LOGGED_IN]?:false
    }

    val userID:Flow<String?> = context.dataStore.data
         .map { Preferences ->
            Preferences[DataStoreKeys.USER_ID]?:""
         }


}