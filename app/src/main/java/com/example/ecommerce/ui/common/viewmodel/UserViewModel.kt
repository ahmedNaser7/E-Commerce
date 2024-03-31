package com.example.ecommerce.ui.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.ecommerce.data.repository.user.UserPreferencesRepositoryImpl
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

// class to save the data from dataStore
// observe on data
// link
class UserViewModel(val userPreferencesRepository:UserPreferencesRepositoryImpl):ViewModel() {

   suspend fun isUserLoggedIn()=userPreferencesRepository.isUserLoggedIn()

   fun setIsLoggedIn(isLogged: Boolean){
      viewModelScope.launch(IO) {
         userPreferencesRepository.saveLoginState(isLogged)
      }
   }

   // Todo : fun save_user_id and fun get_user
}


class UserViewModelFactory(private val userPreferencesRepositoryImpl: UserPreferencesRepositoryImpl):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return UserViewModel(userPreferencesRepositoryImpl) as T
        }
        throw IllegalArgumentException("UNKNOWN ViewModel Class")
    }
}