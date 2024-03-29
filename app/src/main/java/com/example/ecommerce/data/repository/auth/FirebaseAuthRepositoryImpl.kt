package com.example.ecommerce.data.repository.auth


import com.example.ecommerce.data.model.Resources
import com.google.firebase.auth.FirebaseAuth
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
    ): Flow<Resources<String>> {

        val authTask: Flow<Resources<String>> = flow {
            try {
                emit(Resources.Loading())
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                authResult?.user?.let { user ->
                    emit(Resources.Success(user.uid))
                } ?: run {
                    emit(Resources.Error(Exception("Unknown User")))
                }

            } catch (e: Exception) {
                emit(Resources.Error(e))

            }

        }

        return authTask
    }
}