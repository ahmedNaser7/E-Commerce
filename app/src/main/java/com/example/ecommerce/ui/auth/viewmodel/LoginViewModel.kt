package com.example.ecommerce.ui.auth.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.data.repository.user.UserPreferencesRepository
import kotlinx.coroutines.launch

class LoginViewModel
    (private val userPreferencesRepository: UserPreferencesRepository
     ):ViewModel() {


}

@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory(private val userPreferencesRepository: UserPreferencesRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(userPreferencesRepository) as T
        }
          throw IllegalArgumentException("UnKnown ViewModel Class")
    }

}