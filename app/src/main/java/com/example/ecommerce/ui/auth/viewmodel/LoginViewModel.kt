package com.example.ecommerce.ui.auth.viewmodel


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.data.datasource.datastore.AppPreferencesDataSource
import com.example.ecommerce.data.model.Resource
import com.example.ecommerce.data.model.user.UserDetailsModel
import com.example.ecommerce.data.repository.auth.FirebaseAuthRepository
import com.example.ecommerce.data.repository.auth.FirebaseAuthRepositoryImpl
import com.example.ecommerce.data.repository.common.AppDataStoreRepository
import com.example.ecommerce.data.repository.common.AppDataStoreRepositoryImpl
import com.example.ecommerce.data.repository.user.UserPreferencesRepository
import com.example.ecommerce.data.repository.user.UserPreferencesRepositoryImpl
import com.example.ecommerce.domain.toUserDetailsPreferences
import com.example.ecommerce.utils.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject constructor(
    private val appPreferencesRepository: AppDataStoreRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _loginState = MutableSharedFlow<Resource<UserDetailsModel>>()
    val loginState: SharedFlow<Resource<UserDetailsModel>> = _loginState.asSharedFlow()

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")


    private val isLoginIsValid: Flow<Boolean> = combine(email, password) { email, password ->
        email.isValidEmail() && password.length >= 6
    }

    fun login() = viewModelScope.launch(IO) {
        val email = email.value
        val password = password.value
        if (isLoginIsValid.first()) {
            handleLogin {
                firebaseAuthRepository.loginWithEmailAndPassword(email, password)
            }
        } else {
            _loginState.emit(Resource.Error(Exception("Invalid email or password")))
        }
    }


    fun loginWithGoogle(idToken: String) = handleLogin {
        firebaseAuthRepository.loginWithGoogle(idToken)
    }


    fun loginWithFacebook(token: String) = handleLogin {
        firebaseAuthRepository.loginWithFacebook(token)
    }

    private fun handleLogin(loginFlow: suspend () -> Flow<Resource<UserDetailsModel>>) =
        viewModelScope.launch(IO) {
            loginFlow().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _loginState.emit(Resource.Success(resource.data!!))
                        saveLoginState(resource.data)
                    }

                    else -> {
                        _loginState.emit(resource)
                    }
                }
            }
        }


    private fun saveLoginState(userDetailsModel: UserDetailsModel) = viewModelScope.launch(IO) {
        appPreferencesRepository.saveLoginState(true)
        userPreferencesRepository.updateUserDetails(userDetailsModel.toUserDetailsPreferences())
    }


}
