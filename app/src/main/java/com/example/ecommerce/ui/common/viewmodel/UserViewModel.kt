package com.example.ecommerce.ui.common.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.ecommerce.data.datasource.datastore.AppPreferencesDataSource
import com.example.ecommerce.data.model.Resource
import com.example.ecommerce.data.repository.auth.FirebaseAuthRepository
import com.example.ecommerce.data.repository.auth.FirebaseAuthRepositoryImpl
import com.example.ecommerce.data.repository.common.AppDataStoreRepository
import com.example.ecommerce.data.repository.common.AppDataStoreRepositoryImpl
import com.example.ecommerce.data.repository.user.UserFireStoreRepository
import com.example.ecommerce.data.repository.user.UserFireStoreRepositoryImpl
import com.example.ecommerce.data.repository.user.UserPreferencesRepository
import com.example.ecommerce.data.repository.user.UserPreferencesRepositoryImpl
import com.example.ecommerce.domain.toUserDetailsModel
import com.example.ecommerce.domain.toUserDetailsPreferences
import com.example.ecommerce.utils.CrashlyticsUtils
import com.example.ecommerce.utils.CrashlyticsUtils.LISTEN_TO_USER_DETAILS
import com.example.ecommerce.utils.UserDetailsException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// class to save the data from dataStore
// observe on data
// link

@HiltViewModel
class UserViewModel @Inject constructor(
    private val appUserPreferencesRepository: AppDataStoreRepository,
    private val userFireStoreRepository: UserFireStoreRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val authRepo: FirebaseAuthRepository
) : ViewModel() {

    // logout State
    private val logOutState = MutableSharedFlow<Resource<Unit>>()

    val userDetailsState = getUserDetails().stateIn(
        viewModelScope, SharingStarted.Eagerly, null
    )


    init {
        listenToUserDetails()
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUserDetails() =
        userPreferencesRepository.getUserDetails().mapLatest { it.toUserDetailsModel() }

    suspend fun isUserLoggedIn() = appUserPreferencesRepository.isUserLoggedIn()

    private fun listenToUserDetails() = viewModelScope.launch {
        // get id from userDataStoreRepository
        val userId = userPreferencesRepository.getUserId().first()

        if (userId.isEmpty()) return@launch

        userFireStoreRepository.getUserDetails(userId).catch { error ->
            Log.d(TAG, "listenToUserDetails: $error")
            val msg = error.message ?: "Error in ListenToUserDetails"
            CrashlyticsUtils.sendCustomLogToCrashlytics<UserDetailsException>(
                msg, LISTEN_TO_USER_DETAILS to msg
            )
            if (error is UserDetailsException) logOut()
        }.collectLatest { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { userModel ->
                        // save User fireStore model of user data store
                        userPreferencesRepository.updateUserDetails(userModel.toUserDetailsPreferences())
                    }

                }

                else -> {
                    Log.d(TAG, "listenToUserDetails: ${resource.exception?.message}")
                }
            }
        }
    }

    // logOut impl

    private fun logOut() = viewModelScope.launch {
        logOutState.emit(Resource.Loading())
        appUserPreferencesRepository.saveLoginState(false)
        userPreferencesRepository.clearUserPreferences()
        authRepo.logout()
        logOutState.emit(Resource.Success(Unit))
    }

    companion object {
        const val TAG = "UserViewModel"
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "UserViewModel : onCleared: ")
    }

}


