package com.example.ecommerce.ui.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.ecommerce.data.repository.user.AppDataStoreRepositoryImpl
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

// class to save the data from dataStore
// observe on data
// link
class UserViewModel(val appUserPreferencesRepository: AppDataStoreRepositoryImpl) : ViewModel() {

    suspend fun isUserLoggedIn() = appUserPreferencesRepository.isUserLoggedIn()

    fun setIsLoggedIn(isLogged: Boolean) {
        viewModelScope.launch(IO) {
            appUserPreferencesRepository.saveLoginState(isLogged)
        }
    }

}


class UserViewModelFactory(private val userPreferencesRepositoryImpl: AppDataStoreRepositoryImpl) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return UserViewModel(userPreferencesRepositoryImpl) as T
        }
        throw IllegalArgumentException("UNKNOWN ViewModel Class")
    }
}