package com.example.ecommerce.data.datasource.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class AppPreferencesDataSource(private val context: Context) {


    // write
    suspend fun saveLoginState(isLoggedIn:Boolean){
        context.appDataStore.edit {
            it[DataStoreKeys.IS_USER_LOGGED_IN] = isLoggedIn
        }
    }


   // read
    val isUserLoggedIn :Flow<Boolean> = context.appDataStore.data
        .map {
        it[DataStoreKeys.IS_USER_LOGGED_IN]?:false
    }



}