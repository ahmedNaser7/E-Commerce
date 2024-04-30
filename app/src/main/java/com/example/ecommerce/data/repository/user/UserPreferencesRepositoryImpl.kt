package com.example.ecommerce.data.repository.user

import android.content.Context
import com.example.ecommerce.data.datasource.datastore.userDetailsDataStore
import com.example.ecommerce.data.model.user.UserDetailsPreferences
import com.example.ecommerce.data.model.user.userDetailsPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepositoryImpl(
    private val context: Context
) : UserPreferencesRepository {

    override fun getUserDetails(): Flow<UserDetailsPreferences> {
        return context.userDetailsDataStore.data
    }

    override suspend fun updateUserId(userId: String) {
        context.userDetailsDataStore.updateData { userPreferences ->
            userPreferences.toBuilder()
                .setId(userId)
                .build()
        }
    }

    override suspend fun getUserId(): Flow<String> {
        return context.userDetailsDataStore.data.map { userPreferences ->
            userPreferences.id
        }
    }

    override suspend fun clearUserPreferences() {
        context.userDetailsDataStore.updateData { userPreferences ->
            userPreferences.toBuilder()
                .clear()
                .build()
        }
    }

    override suspend fun updateUserDetails(userDetailsPreferences: UserDetailsPreferences) {
        context.userDetailsDataStore.updateData { userPreferences ->
            userDetailsPreferences
        }
    }
}