package com.example.ecommerce.data.repository.common

import kotlinx.coroutines.flow.Flow

interface AppDataStoreRepository {

    suspend fun saveLoginState(isLoggedIn:Boolean)

    suspend fun isUserLoggedIn():Flow<Boolean>

}