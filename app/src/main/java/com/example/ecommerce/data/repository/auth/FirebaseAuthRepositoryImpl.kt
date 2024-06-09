package com.example.ecommerce.data.repository.auth


import com.example.ecommerce.data.model.Resource
import com.example.ecommerce.data.model.user.AuthProvider
import com.example.ecommerce.data.model.user.UserDetailsModel
import com.example.ecommerce.utils.CrashlyticsUtils
import com.example.ecommerce.utils.LoginException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class FirebaseAuthRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : FirebaseAuthRepository {

    override suspend fun loginWithEmailAndPassword(
        email: String, password: String
    ): Flow<Resource<UserDetailsModel>> = login(AuthProvider.EMAIL) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun loginWithGoogle(idToken: String): Flow<Resource<UserDetailsModel>> =
        login(AuthProvider.GOOGLE) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
        }

    override suspend fun loginWithFacebook(token: String): Flow<Resource<UserDetailsModel>> =
        login(AuthProvider.FACEBOOK) {
            val credential = FacebookAuthProvider.getCredential(token)
            auth.signInWithCredential(credential).await()
        }

    private suspend fun login(
        authProvider: AuthProvider,
        signInRequest: suspend () -> AuthResult,
    ): Flow<Resource<UserDetailsModel>> = flow {
        try {
            emit(Resource.Loading())
            val authResult = signInRequest()
            val userId = authResult.user?.uid

            if (userId == null) {
                // handle error user is not found
                val msg = "SingIn User is not found"
                logAuthIssueToCrashLyticsIssues(msg, AuthProvider.EMAIL.name)
                emit(Resource.Error(Exception(msg)))
                return@flow
            }

            if (authResult.user?.isEmailVerified == false) {
                authResult.user?.sendEmailVerification()?.await()
                val msg = "Email not verified ,we sent you an email"
                logAuthIssueToCrashLyticsIssues(msg, authProvider.name)
                emit(Resource.Error(Exception(msg)))
                return@flow
            }

            // get user from fireStore to put it in UserDetailsModel

            val userDoc = fireStore.collection("users").document(userId).get().await()
            if (!userDoc.exists()) {
                val msg = "Logged In User is not found in fireStore"
                logAuthIssueToCrashLyticsIssues(msg, authProvider.name)
                emit(Resource.Error(Exception(msg)))
                return@flow
            }

            val userDetails = userDoc.toObject(UserDetailsModel::class.java)
            userDetails?.let {
                emit(Resource.Success(it))
            } ?: run {
                val msg = "Error mapping user details To UserDetailsModel  userId = $userId "
                logAuthIssueToCrashLyticsIssues(msg, authProvider.name)
                emit(Resource.Error(Exception(msg)))
                return@flow
            }

        } catch (e: Exception) {
            val msg = e.message ?: " Error happened with Logged In User with ${authProvider.name}"
            logAuthIssueToCrashLyticsIssues(msg, authProvider.name)
            emit(Resource.Error(e))
        }


    }

    private fun logAuthIssueToCrashLyticsIssues(msg: String, authProvider: String) {
        CrashlyticsUtils.sendCustomLogToCrashlytics<LoginException>(
            msg,
            CrashlyticsUtils.LOGIN_KEY to msg,
            CrashlyticsUtils.LOGIN_PROVIDER to authProvider
        )
    }

    override fun logout() {
        auth.signOut()
    }

}