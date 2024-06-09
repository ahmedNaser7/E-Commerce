package com.example.ecommerce.ui.auth.fragment

import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.R
import com.example.ecommerce.data.model.Resource
import com.example.ecommerce.databinding.FragmentRegisterBinding
import com.example.ecommerce.ui.auth.getGoogleRequestIntent
import com.example.ecommerce.ui.auth.viewmodel.RegisterViewModel
import com.example.ecommerce.ui.common.fragments.BaseFragment
import com.example.ecommerce.ui.showSnakeBarError
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding,RegisterViewModel>() {

    private val callbackManager: CallbackManager by lazy { CallbackManager.Factory.create() }
    private val loginManager: LoginManager by lazy { LoginManager.getInstance() }
    override val viewModel: RegisterViewModel by viewModels()

    override fun getLayoutId(): Int =R.layout.fragment_register

    override fun init() {
        initListeners()
        initViewModel()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            viewModel.registerState.collect {
                when (it) {
                    is Resource.Loading -> progressDialog.show()
                    is Resource.Success -> {
                        progressDialog.dismiss()
                        val msg = "registered Successfully"
                        view?.showSnakeBarError(msg)
                    }
                    is Resource.Error -> {
                        progressDialog.dismiss()
                        val msg = it.exception?.message ?: "please try again later "
                        view?.showSnakeBarError(msg)
                    }
                }
            }
        }
    }


    private fun initListeners() {
        binding.btnLogin.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnSignUpGoogle.setOnClickListener {
            signUpWithGoogleRequest()
        }
        binding.btnSignUpFacebook.setOnClickListener {
            signUpWithFacebookRequest()
        }
    }

    // handle facebook registration
    private fun signUpWithFacebookRequest() {
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val token = result.accessToken.token
                viewModel.registerWithFacebook(token)
            }

            override fun onCancel() {
                // handle cancel
            }

            override fun onError(error: FacebookException) {
                val msg = error.message ?: getString(R.string.generic_err_msg)
                view?.showSnakeBarError(msg)
                // handle error
            }

        })
        loginManager.logInWithReadPermissions(
            this,
            listOf("email", "public_profile")
        )

    }


    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            } else {
                view?.showSnakeBarError(getString(R.string.google_sign_in_field_msg))
            }
        }


    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            viewModel.registerWithGoogle(account.idToken!!)
        } catch (e: Exception) {
            val msg = e.message ?: getString(R.string.generic_err_msg)
            view?.showSnakeBarError(msg)

        }
    }

    private fun signUpWithGoogleRequest() {
        val signInIntent = getGoogleRequestIntent(requireActivity())
        launcher.launch(signInIntent)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = "Register Fragment"
    }
}