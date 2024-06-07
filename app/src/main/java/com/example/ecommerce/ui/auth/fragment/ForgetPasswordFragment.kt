package com.example.ecommerce.ui.auth.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.data.model.Resource
import com.example.ecommerce.data.repository.auth.FirebaseAuthRepositoryImpl
import com.example.ecommerce.databinding.FragmentForgetPasswordBinding
import com.example.ecommerce.ui.auth.viewmodel.ForgetPasswordViewModel
import com.example.ecommerce.ui.auth.viewmodel.ForgetPasswordViewModelFactory
import com.example.ecommerce.ui.common.view.ProgressDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class ForgetPasswordFragment : BottomSheetDialogFragment() {
    private val progressDialog by lazy { ProgressDialog.CraeteProgressDialog(requireContext()) }
    private val viewModel:ForgetPasswordViewModel by viewModels{
        ForgetPasswordViewModelFactory(FirebaseAuthRepositoryImpl())
    }
    private var _binding:FragmentForgetPasswordBinding?=null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgetPasswordBinding.inflate(layoutInflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        lifecycleScope.launch{
         viewModel.forgetPasswordState.collect{
             when(it){
                 is Resource.Loading -> progressDialog.show()
                 is Resource.Success-> progressDialog.dismiss()
                 is Resource.Error -> progressDialog.dismiss()
             }
         }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}