package com.example.ecommerce.ui.common.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.ecommerce.data.datasource.datastore.AppPreferencesDataSource
import com.example.ecommerce.data.model.Resource
import com.example.ecommerce.data.repository.common.AppDataStoreRepository
import com.example.ecommerce.data.repository.common.AppDataStoreRepositoryImpl
import com.example.ecommerce.data.repository.user.UserFireStoreRepository
import com.example.ecommerce.data.repository.user.UserFireStoreRepositoryImpl
import com.example.ecommerce.utils.CrashlyticsUtils
import com.example.ecommerce.utils.CrashlyticsUtils.LISTEN_TO_USER_DETAILS
import com.example.ecommerce.utils.UserDetailsException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// class to save the data from dataStore
// observe on data
// link

class UserViewModel(
    val appUserPreferencesRepository: AppDataStoreRepository,
    val userFireStoreRepository: UserFireStoreRepository
) : ViewModel() {

    suspend fun isUserLoggedIn() = appUserPreferencesRepository.isUserLoggedIn()

    init {
        listenToUserDetails()
    }

    private fun listenToUserDetails() = viewModelScope.launch {
        // get id from userDataStoreRepository
        val fakeUserId = "123"

        userFireStoreRepository.getUserDetails(fakeUserId).catch { error ->
            Log.d(TAG, "listenToUserDetails: $error")
            val msg = error.message ?: "Error in ListenToUserDetails"
            CrashlyticsUtils.sendCustomLogToCrashlytics<UserDetailsException>(
                msg, LISTEN_TO_USER_DETAILS to msg
            )
            // handle error with log out
        }.collectLatest { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        // save the data to userDataStoreRepository
                    }
                }

                else -> {
                    Log.d(TAG, "listenToUserDetails: ${resource.exception?.message}")
                }
            }
        }
    }

    companion object {
        const val TAG = "UserViewModel"
    }

}


class UserViewModelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {
    private val appUserPreferencesRepositoryImpl = AppDataStoreRepositoryImpl(
        AppPreferencesDataSource(context)
    )
    private val userFireStoreRepository = UserFireStoreRepositoryImpl()

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return UserViewModel(
                appUserPreferencesRepositoryImpl,
                userFireStoreRepository
            ) as T
        }
        throw IllegalArgumentException("UNKNOWN ViewModel Class")
    }
}