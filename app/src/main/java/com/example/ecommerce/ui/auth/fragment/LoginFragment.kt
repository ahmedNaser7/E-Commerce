package com.example.ecommerce.ui.auth.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ecommerce.R
import com.example.ecommerce.data.datasource.datastore.UserPreferencesDataSource
import com.example.ecommerce.data.model.Resources
import com.example.ecommerce.data.repository.auth.FirebaseAuthRepositoryImpl
import com.example.ecommerce.data.repository.user.UserPreferencesRepositoryImpl
import com.example.ecommerce.databinding.FragmentLoginBinding
import com.example.ecommerce.ui.auth.viewmodel.LoginViewModel
import com.example.ecommerce.ui.auth.viewmodel.LoginViewModelFactory
import com.example.ecommerce.ui.common.view.ProgressDialog
import com.example.ecommerce.ui.showSnakeBarError
import com.example.ecommerce.utils.CrashlyticsUtils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.security.auth.login.LoginException


class LoginFragment : Fragment() {

    private val progressDialog by lazy {
        ProgressDialog.CraeteProgressDialog(requireContext())
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(
            userPreferencesRepository = UserPreferencesRepositoryImpl(
                userPreferencesDataSource = UserPreferencesDataSource(requireActivity())
            ),
            firebaseAuthRepository = FirebaseAuthRepositoryImpl(FirebaseAuth.getInstance())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = loginViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            loginViewModel.loginState.collect { resources ->
                when (resources) {
                    is Resources.Loading -> progressDialog.show()
                    is Resources.Success -> {
                        progressDialog.dismiss()
                        view?.showSnakeBarError(resources.data!!)
                    }

                    is Resources.Error -> {
                        progressDialog.dismiss()
                        val msg =
                            resources.exception?.message ?: getString(R.string.generic_err_msg)
                        view?.showSnakeBarError(msg)
                        logAuthIssueToCrashlytics(msg, "Login Error")
                    }
                }
            }
        }
    }

    private fun logAuthIssueToCrashlytics(msg: String, provider: String) {
        CrashlyticsUtils.sendCustomLogToCrashlytics<LoginException>(
            msg,
            CrashlyticsUtils.LOGIN_KEY to msg,
            CrashlyticsUtils.LOGIN_PROVIDER to provider
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    companion object {
        const val TAG = "LoginFragment"
    }
}