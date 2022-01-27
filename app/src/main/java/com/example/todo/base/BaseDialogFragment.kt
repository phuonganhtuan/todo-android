package com.example.todo.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.example.todo.utils.windowWidth

abstract class BaseDialogFragment<VB : ViewBinding> : DialogFragment() {

    protected lateinit var viewBinding: VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = inflateViewBinding(container, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return viewBinding.root
    }

    abstract fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): VB

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            (windowWidth * 0.8).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    protected fun showToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
