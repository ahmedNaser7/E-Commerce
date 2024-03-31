package com.example.ecommerce.data.repository.auth


import com.example.ecommerce.data.model.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class FirebaseAuthRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : FirebaseAuthRepository {

    override suspend fun loginWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<Resource<String>> {

        val authTask: Flow<Resource<String>> = flow {
            try {
                emit(Resource.Loading())
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                authResult?.user?.let { user ->
                    emit(Resource.Success(user.uid))
                } ?: run {
                    emit(Resource.Error(Exception("Unknown User")))
                }

            } catch (e: Exception) {
                emit(Resource.Error(e))

            }

        }

        return authTask
    }

    override suspend fun loginWithGoogle(idToken: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val task = auth.signInWithCredential(credential).await()
            task.user?.let{ user ->
                emit(Resource.Success(user.uid))
            } ?: run {
                emit(Resource.Error(Exception("Unknown User")))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e))

        }

    }

}