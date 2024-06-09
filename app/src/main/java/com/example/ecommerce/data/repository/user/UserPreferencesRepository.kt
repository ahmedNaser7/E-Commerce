package com.example.ecommerce.data.repository.user

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    // get user of proto data store
    fun getUserDetails(): Flow<UserDetailsPreferences>

    suspend fun updateUserId(userId: String)

    suspend fun isUserLoggedIn():Flow<Boolean>
    fun getUserId():Flow<String?>


}