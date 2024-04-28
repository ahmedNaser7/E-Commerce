package com.example.ecommerce.data.repository.user

import kotlinx.coroutines.flow.Flow

interface AppDataStoreRepository {

    suspend fun saveLoginState(isLoggedIn:Boolean)

    suspend fun isUserLoggedIn():Flow<Boolean>

}