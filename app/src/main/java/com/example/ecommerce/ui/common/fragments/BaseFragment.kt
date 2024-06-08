package com.example.ecommerce.ui.common.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.ecommerce.BR
import com.example.ecommerce.ui.common.view.ProgressDialog


abstract class BaseFragment<DB : ViewDataBinding , VM : ViewModel> : Fragment() {

    val progressDialog by lazy { ProgressDialog.CraeteProgressDialog(requireContext()) }

    protected abstract val viewModel:VM
    protected var _binding: DB? = null
    protected val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater,getLayoutId(),container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doDataBinding()
        init()
    }

    // Abstract function to get the layout ID
    @LayoutRes
    abstract fun getLayoutId(): Int
    abstract fun init()
    private fun doDataBinding(){
        binding.lifecycleOwner = viewLifecycleOwner
        binding.setVariable(
            BR.viewModel,viewModel
        )
        binding.executePendingBindings()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}