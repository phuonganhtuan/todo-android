package com.trustedapp.todolist.planner.reminders.base

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.trustedapp.todolist.planner.reminders.utils.windowHeight
import com.trustedapp.todolist.planner.reminders.utils.windowWidth
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialogFragment<VB : ViewBinding> : BottomSheetDialogFragment() {

    protected lateinit var viewBinding: VB

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener { dialog: DialogInterface ->
            val bottomDialog = dialog as BottomSheetDialog
            val bottomSheet =
                bottomDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            val bottomSheetBehavior: BottomSheetBehavior<*> =
                BottomSheetBehavior.from(bottomSheet!!)
            bottomSheetBehavior.peekHeight = windowHeight
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
        return bottomSheetDialog
    }

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
            windowWidth,
            windowHeight
        )
    }

    protected fun showToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
