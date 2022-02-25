package com.trustedapp.todolist.planner.reminders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.trustedapp.todolist.planner.reminders.base.BaseFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentNewTaskBinding
import com.trustedapp.todolist.planner.reminders.databinding.FragmentNotiReminderBinding
import com.trustedapp.todolist.planner.reminders.screens.newtask.NewTaskViewModel
import com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.NotiReminderViewModel
import com.trustedapp.todolist.planner.reminders.utils.hide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotiReminderFragment : BaseFragment<FragmentNotiReminderBinding>() {
    private val viewModel: NotiReminderViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentNotiReminderBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() = with(viewBinding){
        setupToolbar()
        setupEvents()
    }

    private fun setupToolbar() = with(viewBinding.layoutTop) {
        textTitle.text = getString(R.string.noti_and_reminder_title)
        button1.setImageResource(R.drawable.ic_arrow_left)
        button4.hide()
        button2.hide()
        button3.hide()
    }

    private fun setupEvents() = with(viewBinding) {
        layoutTop.button1.setOnClickListener { activity?.finish() }

        lnNotiHelp.setOnClickListener { findNavController().navigate(R.id.toNotificationHelp) }
        lnDefaultTaskReminderType.setOnClickListener { findNavController().navigate(R.id.toDefaultReminderType) }
        lnDefaultNotiRington.setOnClickListener { findNavController().navigate(R.id.toDefautlNotificationRingtone) }
    }
}