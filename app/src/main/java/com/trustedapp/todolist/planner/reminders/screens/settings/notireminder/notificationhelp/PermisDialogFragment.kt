package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.notificationhelp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDialogFragment
import com.trustedapp.todolist.planner.reminders.base.BaseFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentNotificationHelpBinding
import com.trustedapp.todolist.planner.reminders.databinding.FragmentPermisDialogBinding

class PermisDialogFragment : BaseDialogFragment<FragmentPermisDialogBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPermisDialogBinding.inflate(layoutInflater, container, false)


}