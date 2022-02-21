package com.trustedapp.todolist.planner.reminders.screens.notireminder

import android.os.Bundle
import androidx.activity.viewModels
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.databinding.ActivityNotiReminderBinding
import com.trustedapp.todolist.planner.reminders.utils.hide

class NotiReminderActivity: BaseActivity<ActivityNotiReminderBinding>() {
    private val viewModel: NotiReminderViewModel by viewModels()

    override fun inflateViewBinding() = ActivityNotiReminderBinding.inflate(layoutInflater)
    override fun onActivityReady() {

    }

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }


}