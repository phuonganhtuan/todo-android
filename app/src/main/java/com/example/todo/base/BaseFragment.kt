package com.example.todo.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    protected lateinit var viewBinding: VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = inflateViewBinding(container, savedInstanceState)
        return viewBinding.root
    }

    abstract fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): VB

    protected fun showToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    protected fun showSnackMessage(message: String) {
        Snackbar.make(viewBinding.root, message, Snackbar.LENGTH_SHORT)
    }
}
