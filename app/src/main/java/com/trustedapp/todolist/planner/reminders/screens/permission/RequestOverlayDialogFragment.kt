package com.trustedapp.todolist.planner.reminders.screens.permission

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import com.trustedapp.todolist.planner.reminders.base.BaseDialogFragment
import com.trustedapp.todolist.planner.reminders.databinding.LayoutRequestOverlayBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestOverlayDialogFragment : BaseDialogFragment<LayoutRequestOverlayBinding>() {

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LayoutRequestOverlayBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEvents()
    }

    private fun setupEvents() = with(viewBinding) {
        buttonCancel.setOnClickListener {
            dismiss()
        }
        buttonApply.setOnClickListener {
            if (!Settings.canDrawOverlays(requireContext())) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + requireContext().packageName)
                )
                startActivityForResult(intent, 0)
            }
            dismiss()
        }
    }
}