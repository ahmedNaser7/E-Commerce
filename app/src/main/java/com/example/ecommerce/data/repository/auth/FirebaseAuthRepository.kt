package com.example.ecommerce.data.repository.auth

import com.example.ecommerce.data.model.Resources
import kotlinx.coroutines.flow.Flow

interface FirebaseAuthRepository {
    suspend fun loginWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<Resources<String>>
}