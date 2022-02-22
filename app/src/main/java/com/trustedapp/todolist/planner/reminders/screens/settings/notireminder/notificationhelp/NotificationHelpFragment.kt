package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.notificationhelp

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentNotificationHelpBinding
import com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.NotiReminderViewModel
import com.trustedapp.todolist.planner.reminders.utils.hide

class NotificationHelpFragment : BaseFragment<FragmentNotificationHelpBinding>() {

    private val viewModel: NotiReminderViewModel by activityViewModels()

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentNotificationHelpBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView(){
        setupToolbar()
        setupEvents()
    }

    private fun setupToolbar() = with(viewBinding.layoutTop) {
        textTitle.text = resources.getString(R.string.notification_help)
        button1.setImageResource(R.drawable.ic_arrow_left)
        button4.hide()
        button2.hide()
        button3.hide()
    }

    private fun setupEvents() = with(viewBinding) {
        layoutTop.button1.setOnClickListener { findNavController().popBackStack() }

        swFloatingWindow.setOnClickListener { onChangeFloatWindow() }
    }

    private fun onChangeFloatWindow(){
        findNavController().navigate(R.id.toPermisDialog)
    }
}