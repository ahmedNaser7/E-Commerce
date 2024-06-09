package com.example.ecommerce.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.data.model.Resource
import com.example.ecommerce.data.repository.auth.FirebaseAuthRepository
import com.example.ecommerce.utils.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository
):ViewModel() {

    private val _forgetPasswordState = MutableSharedFlow<Resource<String>>()
    val forgetPasswordState = _forgetPasswordState.asSharedFlow()

    val email = MutableStateFlow("")

    fun sendEmailReset(){
        if(email.value.isValidEmail()){
            viewModelScope.launch {
              firebaseAuthRepository.sendPasswordWithEmail(email.value).collect{
                  _forgetPasswordState.emit(it)
              }
            }
        }
    }
}

