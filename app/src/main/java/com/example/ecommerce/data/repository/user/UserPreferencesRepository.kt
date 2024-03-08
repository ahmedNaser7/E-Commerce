package com.example.ecommerce.data.repository.user

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    suspend fun isUserLoggedIn():Flow<Boolean>

    suspend fun saveLoginState(isLoggedIn:Boolean)


}