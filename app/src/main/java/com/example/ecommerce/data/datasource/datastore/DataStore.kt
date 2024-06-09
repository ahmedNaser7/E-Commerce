package com.example.ecommerce.data.datasource.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ecommerce.data.datasource.datastore.DataStoreKeys.E_COMMERCE_PREFERENCES
import com.example.ecommerce.data.datasource.datastore.DataStoreKeys.USER_DETAILS_PREFERENCES_PB
import com.example.ecommerce.data.model.user.UserDetailsPreferences
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream


object DataStoreKeys {
    const val E_COMMERCE_PREFERENCES = "e_commerce_preferences"
    const val USER_DETAILS_PREFERENCES_PB = "user_details_preferences.pb"
    val IS_USER_LOGGED_IN = booleanPreferencesKey("is_user_logged_in")
}

val Context.appDataStore:DataStore<Preferences> by preferencesDataStore(name = E_COMMERCE_PREFERENCES)

val Context.userDetailsDataStore by dataStore(
    fileName = USER_DETAILS_PREFERENCES_PB,
    serializer = UserDetailsPreferencesSerializer
)

object UserDetailsPreferencesSerializer : Serializer<UserDetailsPreferences> {

    override val defaultValue: UserDetailsPreferences = UserDetailsPreferences.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): UserDetailsPreferences = try {
        // readFrom is already called on the data store background thread
        UserDetailsPreferences.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
        throw CorruptionException("Cannot read proto.", exception)
    }

    override suspend fun writeTo(t: UserDetailsPreferences, output: OutputStream) {
        // writeTo is already called on the data store background thread
        t.writeTo(output)
    }
}
