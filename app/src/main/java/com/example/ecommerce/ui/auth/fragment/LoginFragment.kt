package com.example.ecommerce.ui.auth.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.ecommerce.R
import com.example.ecommerce.data.datasource.datastore.UserPreferencesDataSource
import com.example.ecommerce.data.repository.user.UserPreferencesRepositoryImpl
import com.example.ecommerce.ui.auth.viewmodel.LoginViewModel
import com.example.ecommerce.ui.auth.viewmodel.LoginViewModelFactory



class LoginFragment : Fragment() {

    val loginViewModel: LoginViewModel by viewModels{
        LoginViewModelFactory(userPreferencesRepository = UserPreferencesRepositoryImpl(
            userPreferencesDataSource = UserPreferencesDataSource(requireActivity())))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }


    companion object{
        const val TAG = "LoginFragment"
    }
}