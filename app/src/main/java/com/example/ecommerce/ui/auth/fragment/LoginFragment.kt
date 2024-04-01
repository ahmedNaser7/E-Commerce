package com.example.ecommerce.ui.auth.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ecommerce.BuildConfig
import com.example.ecommerce.R
import com.example.ecommerce.data.datasource.datastore.UserPreferencesDataSource
import com.example.ecommerce.data.model.Resource
import com.example.ecommerce.data.repository.auth.FirebaseAuthRepositoryImpl
import com.example.ecommerce.data.repository.user.UserPreferencesRepositoryImpl
import com.example.ecommerce.databinding.FragmentLoginBinding
import com.example.ecommerce.ui.auth.viewmodel.LoginViewModel
import com.example.ecommerce.ui.auth.viewmodel.LoginViewModelFactory
import com.example.ecommerce.ui.common.view.ProgressDialog
import com.example.ecommerce.ui.home.MainActivity
import com.example.ecommerce.ui.showSnakeBarError
import com.example.ecommerce.utils.CrashlyticsUtils
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import javax.security.auth.login.LoginException


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(
            userPreferencesRepository = UserPreferencesRepositoryImpl(
                userPreferencesDataSource = UserPreferencesDataSource(requireActivity())
            ),
            firebaseAuthRepository = FirebaseAuthRepositoryImpl(FirebaseAuth.getInstance())
        )
    }

    private val progressDialog by lazy { ProgressDialog.CraeteProgressDialog(requireContext()) }

    lateinit var callbackManager: CallbackManager
    lateinit var loginManager: LoginManager
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = loginViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()
        loginManager = LoginManager.getInstance()
        initListeners()
        initViewModel()
    }

    private fun initListeners() {
        binding.btnSignInGoogle.setOnClickListener {
            loginWithGoogleRequest()
        }
        binding.btnSignInFacebook.setOnClickListener {
            loginWithFacebookRequest()
        }
    }

    private fun loginWithFacebookRequest() {
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val token = result.accessToken.token
                handleFacebookAccess(token)
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

    private fun handleFacebookAccess(token: String) {
        loginViewModel.loginWithFacebook(token)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
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
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.WEB_CLIENT_ID).requestEmail().requestProfile()
            .requestServerAuthCode(BuildConfig.WEB_CLIENT_ID).build()

        val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        googleSignInClient.signOut()
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.d(TAG, account.email.toString())
            firebaseAuthWithGoogle(account.idToken!!)

        } catch (e: Exception) {
            val msg = e.message ?: getString(R.string.generic_err_msg)
            view?.showSnakeBarError(msg)
            logAuthIssueToCrashlytics(msg, "Google")
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        loginViewModel.loginWithGoogle(idToken)
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            loginViewModel.loginState.collect { resources ->
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
        requireContext().startActivity(Intent(requireContext(), MainActivity::class.java).apply {
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