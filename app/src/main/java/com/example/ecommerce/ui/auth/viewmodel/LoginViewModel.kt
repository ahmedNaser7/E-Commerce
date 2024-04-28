package com.example.ecommerce.ui.auth.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.data.model.Resource
import com.example.ecommerce.data.repository.auth.FirebaseAuthRepository
import com.example.ecommerce.data.repository.user.AppDataStoreRepository
import com.example.ecommerce.utils.isValidEmail
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LoginViewModel
    (
    private val appPreferencesRepository: AppDataStoreRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository
) : ViewModel() {

    private val _loginState = MutableSharedFlow<Resource<String>>()
    val loginState = _loginState.asSharedFlow()

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")


    private val isLoginIsValid: Flow<Boolean> = combine(email, password) { email, password ->
        email.isValidEmail() && password.length >= 6
    }

    fun login() = viewModelScope.launch(IO) {
        val email = email.value
        val password = password.value
        if (isLoginIsValid.first()) {
            firebaseAuthRepository.loginWithEmailAndPassword(email, password).onEach { resource ->
                when (resource) {
                    is Resource.Loading -> _loginState.emit(Resource.Loading())
                    is Resource.Success -> {
                        //TODO get user details from the user id
                        _loginState.emit(Resource.Success(resource.data!!))
                    }

                    is Resource.Error -> _loginState.emit(Resource.Error(resource.exception!!))
                }
            }.launchIn(viewModelScope)
        } else {
            _loginState.emit(Resource.Error(Exception("Invalid email or password")))
        }
    }


    fun loginWithGoogle(idToken: String) = viewModelScope.launch(IO) {
        firebaseAuthRepository.loginWithGoogle(idToken).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _loginState.emit(Resource.Loading())
                is Resource.Success -> {
                    //TODO get user details from the user id
                    _loginState.emit(Resource.Success(resource.data!!))
                }

                is Resource.Error -> _loginState.emit(Resource.Error(resource.exception!!))
            }
        }.launchIn(viewModelScope)
    }
    
    
    fun loginWithFacebook(token:String) = viewModelScope.launch(IO) {
        firebaseAuthRepository.loginWithFacebook(token).onEach { resource -> 
            when(resource){
                is Resource.Loading -> _loginState.emit(Resource.Loading())
                is Resource.Success -> _loginState.emit(Resource.Success(resource.data!!))
                is Resource.Error -> _loginState.emit(Resource.Error(resource.exception!!))
            }
        }
    }

    private fun saveLoginState(isLogin: Boolean) = viewModelScope.launch(IO) {
        appPreferencesRepository.saveLoginState(isLogin)
    }


}

@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory(
    private val appPreferencesRepository: AppDataStoreRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(appPreferencesRepository, firebaseAuthRepository) as T
        }
        throw IllegalArgumentException("UnKnown ViewModel Class")
    }

}