package com.example.ecommerce.data.repository.user

import com.example.ecommerce.data.model.user.UserDetailsPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    // get user of proto data store
    fun getUserDetails(): Flow<UserDetailsPreferences>

    suspend fun updateUserId(userId: String)

    suspend fun getUserId(): Flow<String>

    //clear user from proto data store
    suspend fun clearUserPreferences()

    // update user details in proto data store
    // like add new name or new email in same user model
    suspend fun updateUserDetails(userDetailsPreferences: UserDetailsPreferences)

}