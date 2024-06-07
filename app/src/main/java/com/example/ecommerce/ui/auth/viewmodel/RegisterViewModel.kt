package com.example.ecommerce.ui.auth.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.data.model.Resource
import com.example.ecommerce.data.model.user.UserDetailsModel
import com.example.ecommerce.data.repository.auth.FirebaseAuthRepository
import com.example.ecommerce.data.repository.auth.FirebaseAuthRepositoryImpl
import com.example.ecommerce.utils.isValidEmail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


class RegisterViewModel(
    private val firebaseAuthRepository: FirebaseAuthRepository
) : ViewModel() {

    val name = MutableStateFlow("")
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")

    private val _registerState = MutableSharedFlow<Resource<UserDetailsModel>>()
    val registerState: SharedFlow<Resource<UserDetailsModel>> = _registerState.asSharedFlow()

    private val isRegisterValid: Flow<Boolean> =
        combine(name, email, password, confirmPassword) { name, email, password, confirmPassword ->
            name.isNotEmpty() && email.isValidEmail() && password.length >= 6 && confirmPassword.isNotEmpty() && password == confirmPassword
        }

    fun register() = viewModelScope.launch {
        if (isRegisterValid.first()) {
            firebaseAuthRepository.registerWithEmailAndPassword(
                name.value,
                email.value,
                password.value
            ).collect{
                _registerState.emit(it)
            }
        } else {
            val msg = "Error in Register View model "
            Log.d(TAG, msg)
        }

    }

    fun registerWithGoogle(idToken:String) = viewModelScope.launch {
        firebaseAuthRepository.registerWithGoogle(idToken)
    }

    fun registerWithFacebook(token:String)=viewModelScope.launch {
        firebaseAuthRepository.registerWithFacebook(token)
    }


    companion object {
        const val TAG = "Register View Model"
    }

}

@Suppress("UNCHECKED_CAST")
class RegisterViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    private val firebaseAuthRepository = FirebaseAuthRepositoryImpl()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(
                firebaseAuthRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}