package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.defaulttaskremindertype

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.trustedapp.todolist.planner.reminders.base.BaseDialogFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentDefaultReminderTypeDialogBinding

class DefaultReminderTypeDialog : BaseDialogFragment<FragmentDefaultReminderTypeDialogBinding>() {
    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentDefaultReminderTypeDialogBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView(){

    }
}