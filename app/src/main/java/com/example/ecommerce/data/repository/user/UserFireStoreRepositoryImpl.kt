package com.example.ecommerce.data.repository.user

import com.example.ecommerce.data.model.Resource
import com.example.ecommerce.data.model.user.UserDetailsModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserFireStoreRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore
) : UserFireStoreRepository {
    override suspend fun getUserDetails(id: String): Flow<Resource<UserDetailsModel>> =
        callbackFlow {
            send(Resource.Loading())
            val documentPath = "users/$id"
            val document = fireStore.document(documentPath)
            val listener = document.addSnapshotListener { value, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
              value?.toObject(UserDetailsModel::class.java)?.let { userModel ->
                  if(userModel.disabled == true) {
                      close(AccountDisabledException("Account is disabled"))
                      return@addSnapshotListener
                  }
                  trySend(Resource.Success(userModel))
              }?.run {
                  close(UserNotFoundException("User not found"))
              }
            }

            awaitClose { listener.remove() }
        }
}

class AccountDisabledException(message: String) : Exception(message)
class UserNotFoundException(message: String) : Exception(message)
