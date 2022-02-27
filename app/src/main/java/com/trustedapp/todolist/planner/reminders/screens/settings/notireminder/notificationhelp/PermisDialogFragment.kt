package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.notificationhelp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDialogFragment
import com.trustedapp.todolist.planner.reminders.base.BaseFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentNotificationHelpBinding
import com.trustedapp.todolist.planner.reminders.databinding.FragmentPermisDialogBinding
import com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.NotiReminderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PermisDialogFragment : BaseDialogFragment<FragmentPermisDialogBinding>() {
    private val viewModel: NotiReminderViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPermisDialogBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEvents()
        observeData()
    }

    private fun setupEvents() = with(viewBinding){
        swPermis.setOnClickListener { onChangeFloatWindow() }
    }

    override fun onResume() {
        super.onResume()
        viewModel.setIsFloatWindow(Settings.canDrawOverlays(context))
    }

    private fun observeData() = with(viewModel){
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isFloatingWindow.collect {
                    viewBinding.apply {
                        swPermis.isChecked = it
                    }
                }
            }
        }
    }

    private fun onChangeFloatWindow() = with(viewBinding){
        swPermis.isChecked = !swPermis.isChecked
        requestPermissions()
    }

    private fun requestPermissions() {
//        if (!Settings.canDrawOverlays(context)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + activity?.packageName)
            )
            startActivityForResult(intent, 0)
//        }
    }
}