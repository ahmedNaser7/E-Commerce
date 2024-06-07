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
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.BuildConfig
import com.example.ecommerce.R
import com.example.ecommerce.data.model.Resource
import com.example.ecommerce.databinding.FragmentRegisterBinding
import com.example.ecommerce.ui.auth.viewmodel.RegisterViewModel
import com.example.ecommerce.ui.auth.viewmodel.RegisterViewModelFactory
import com.example.ecommerce.ui.common.view.ProgressDialog
import com.example.ecommerce.ui.home.HomeActivity
import com.example.ecommerce.ui.showSnakeBarError
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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class RegisterFragment : Fragment() {

    private val progressDialog by lazy { ProgressDialog.CraeteProgressDialog(requireContext()) }

    private val viewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(requireContext())
    }

    private val callbackManager: CallbackManager by lazy {   CallbackManager.Factory.create() }
    private val loginManager: LoginManager by lazy {  LoginManager.getInstance() }
    lateinit var auth: FirebaseAuth


    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initViewModel()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            viewModel.registerState.collect {
                when (it) {
                    is Resource.Error -> {
                        progressDialog.dismiss()
                        val msg = it.exception?.message ?: "please try again later "
                        view?.showSnakeBarError(msg)
                    }

                    is Resource.Loading -> progressDialog.show()
                    is Resource.Success -> {
                        progressDialog.dismiss()
                        // show dialog
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

    private fun signUpWithFacebookRequest() {
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
                // handle error
            }

        })
        loginManager.logInWithReadPermissions(
            this,
            listOf("email", "public_profile")
        )

    }

    private fun handleFacebookAccess(token: String) {
         viewModel.registerWithFacebook(token)
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
            Log.d(TAG, account.email.toString())
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: Exception) {
            // handle error
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        viewModel.registerWithGoogle(idToken)
    }


    private fun signUpWithGoogleRequest() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.WEB_CLIENT_ID).requestEmail().requestProfile()
            .requestServerAuthCode(BuildConfig.WEB_CLIENT_ID).build()

        val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        googleSignInClient.signOut()
        val signInIntent = googleSignInClient.signInIntent
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