package com.example.ecommerce.data.repository.user

import com.example.ecommerce.data.model.user.UserDetailsPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun getUserDetails(): Flow<UserDetailsPreferences>
    suspend fun updateUserId(userId: String)
    suspend fun getUserId(): Flow<String>
    suspend fun clearUserPreferences()
    suspend fun updateUserDetails(userDetailsPreferences: UserDetailsPreferences)
}