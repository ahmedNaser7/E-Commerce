package com.example.ecommerce.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ecommerce.data.datastore.DataStoreKeys.E_COMMERCE_PREFERENCES


object DataStoreKeys {
    const val E_COMMERCE_PREFERENCES = "e_commerce_preferences"
    val IS_USER_LOGGED_IN = booleanPreferencesKey("is_user_logged_in")

}

val Context.dataStore:DataStore<Preferences> by preferencesDataStore(name = E_COMMERCE_PREFERENCES)
