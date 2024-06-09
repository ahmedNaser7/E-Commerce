package com.example.ecommerce.data.repository.auth

import com.example.ecommerce.data.model.Resource
import com.example.ecommerce.data.model.user.UserDetailsModel
import kotlinx.coroutines.flow.Flow

interface FirebaseAuthRepository {
    suspend fun loginWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<Resource<UserDetailsModel>>

    suspend fun loginWithGoogle(
        idToken:String
    ):Flow<Resource<UserDetailsModel>>

    suspend fun loginWithFacebook(
        token:String
    ):Flow<Resource<UserDetailsModel>>


    suspend fun registerWithEmailAndPassword(
        name :String,
        email: String,
        password: String,
    ):Flow<Resource<UserDetailsModel>>

    suspend fun registerWithGoogle(
        idToken:String
    ):Flow<Resource<UserDetailsModel>>

    suspend fun registerWithFacebook(
        token:String
    ):Flow<Resource<UserDetailsModel>>


    suspend fun sendPasswordWithEmail(
        email: String
    ):Flow<Resource<String>>

    fun logout()
}