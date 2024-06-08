package com.example.ecommerce.ui.auth.fragment

import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.R
import com.example.ecommerce.data.model.Resource
import com.example.ecommerce.databinding.FragmentLoginBinding
import com.example.ecommerce.ui.auth.getGoogleRequestIntent
import com.example.ecommerce.ui.auth.viewmodel.LoginViewModel
import com.example.ecommerce.ui.auth.viewmodel.LoginViewModelFactory
import com.example.ecommerce.ui.common.fragments.BaseFragment
import com.example.ecommerce.ui.home.HomeActivity
import com.example.ecommerce.ui.showSnakeBarError
import com.example.ecommerce.utils.CrashlyticsUtils
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch
import javax.security.auth.login.LoginException


class LoginFragment : BaseFragment<FragmentLoginBinding,LoginViewModel>() {

    private val callbackManager: CallbackManager by lazy { CallbackManager.Factory.create() }
    private val loginManager: LoginManager by lazy { LoginManager.getInstance() }

    override val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(requireContext())
    }

    override fun getLayoutId(): Int = R.layout.fragment_login

    override fun init() {
        initListeners()
        initViewModel()
    }


    private fun initListeners() {
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.btnSignInGoogle.setOnClickListener {
            loginWithGoogleRequest()
        }
        binding.btnSignInFacebook.setOnClickListener {
            loginWithFacebookRequest()
        }
        binding.btnForgotPassword.setOnClickListener {
            ForgetPasswordFragment()
                .show(parentFragmentManager, "forget-password")
        }
    }

    private fun loginWithFacebookRequest() {
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val token = result.accessToken.token
                viewModel.loginWithFacebook(token)
            }

            override fun onCancel() {
                // handle cancel
            }

            override fun onError(error: FacebookException) {
                val msg = error.message ?: getString(R.string.generic_err_msg)
                view?.showSnakeBarError(msg)
                logAuthIssueToCrashlytics(msg, "Facebook")
            }

        })
        loginManager.logInWithReadPermissions(
            this,
            listOf("email", "public_profile")
        )

    }


    // ActivityResultLauncher for the sign-in intent
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            } else {
                view?.showSnakeBarError(getString(R.string.google_sign_in_field_msg))
            }
        }

    private fun loginWithGoogleRequest() {
        val signInIntent = getGoogleRequestIntent(requireActivity())
        launcher.launch(signInIntent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.d(TAG, account.email.toString())
            viewModel.loginWithGoogle(account.idToken!!)

        } catch (e: Exception) {
            val msg = e.message ?: getString(R.string.generic_err_msg)
            view?.showSnakeBarError(msg)
            logAuthIssueToCrashlytics(msg, "Google")
        }
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            viewModel.loginState.collect { resources ->
                when (resources) {
                    is Resource.Loading -> progressDialog.show()
                    is Resource.Success -> {
                        progressDialog.dismiss()
                        goToHome()
                    }

                    is Resource.Error -> {
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

    private fun goToHome() {
        requireContext().startActivity(Intent(requireContext(), HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
        requireActivity().finish()
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