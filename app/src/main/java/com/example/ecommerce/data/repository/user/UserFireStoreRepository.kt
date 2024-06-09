package com.example.ecommerce.data.repository.user

import com.example.ecommerce.data.model.Resource
import com.example.ecommerce.data.model.user.UserDetailsModel
import kotlinx.coroutines.flow.Flow

interface UserFireStoreRepository {
    // read and write from firestore and cache the data in model
    // to avoid the price of Firebase

    suspend fun getUserDetails(id: String): Flow<Resource<UserDetailsModel>>

}