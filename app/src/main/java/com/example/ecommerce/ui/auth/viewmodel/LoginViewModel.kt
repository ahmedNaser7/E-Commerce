package com.example.ecommerce.ui.auth.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.data.model.Resource
import com.example.ecommerce.data.repository.auth.FirebaseAuthRepository
import com.example.ecommerce.data.repository.user.UserPreferencesRepository
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
    private val userPreferencesRepository: UserPreferencesRepository,
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
            firebaseAuthRepository.loginWithEmailAndPassword(email, password).onEach { resources ->
                when (resources) {
                    is Resource.Loading -> _loginState.emit(Resource.Loading())
                    is Resource.Success -> {
                        //TODO get user details from the user id
                        _loginState.emit(Resource.Success(resources.data!!))
                    }
                    is Resource.Error -> _loginState.emit(Resource.Error(resources.exception!!))
                }
            }.launchIn(viewModelScope)
        } else {
            _loginState.emit(Resource.Error(Exception("Invalid email or password")))
        }
    }


    fun loginWithGoogle(idToken: String) = viewModelScope.launch(IO) {
        firebaseAuthRepository.loginWithGoogle(idToken).onEach { resources ->
            when (resources) {
                is Resource.Loading -> _loginState.emit(Resource.Loading())
                is Resource.Success -> {
                    //TODO get user details from the user id
                    _loginState.emit(Resource.Success(resources.data!!))
                }
                is Resource.Error -> _loginState.emit(Resource.Error(resources.exception!!))
            }
        }.launchIn(viewModelScope)
    }


}

@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(userPreferencesRepository, firebaseAuthRepository) as T
        }
        throw IllegalArgumentException("UnKnown ViewModel Class")
    }

}