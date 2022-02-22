package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.defaultnotificationringtone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentDefautlNotificationRingtoneBinding
import com.trustedapp.todolist.planner.reminders.databinding.FragmentNotiReminderBinding
import com.trustedapp.todolist.planner.reminders.utils.*

class DefautlNotificationRingtone : BaseFragment<FragmentDefautlNotificationRingtoneBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentDefautlNotificationRingtoneBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupEvents()
    }

    private fun setupToolbar() = with(viewBinding.layoutTop) {
        textTitle.text = getString(R.string.default_notification_rington)
        button1.setImageResource(R.drawable.ic_arrow_left)
        button4.hide()
        button2.gone()
        button3.gone()
    }

    private fun setupEvents() = with(viewBinding) {
        layoutTop.button1.setOnClickListener { findNavController().popBackStack() }

    }
}