package com.example.ecommerce.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.ecommerce.data.repository.user.UserPreferencesRepositoryImpl

// class to save the data from dataStore
// observe on data
// link
class UserViewModel(val repo:UserPreferencesRepositoryImpl):ViewModel() {

   suspend fun isUserLoggedIn()=repo.isUserLoggedIn()

}


class UserViewModelFactory(private val userPreferencesRepositoryImpl: UserPreferencesRepositoryImpl):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return UserViewModel(userPreferencesRepositoryImpl) as T
        }
        throw IllegalArgumentException("UNKNOWN ViewModel Class")
    }
}