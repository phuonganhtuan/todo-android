package com.trustedapp.todolist.planner.reminders.screens.settings.timeformat

import android.os.Bundle
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.databinding.ActivityFirstDayOfWeekBinding
import com.trustedapp.todolist.planner.reminders.databinding.ActivityTimeFormatBinding
import com.trustedapp.todolist.planner.reminders.utils.hide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimeFormatActivity: BaseActivity<ActivityTimeFormatBinding>() {

    override fun inflateViewBinding() = ActivityTimeFormatBinding.inflate(layoutInflater)

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {
        initViews()
        setupEvents()
    }

    private fun initViews() = with(viewBinding) {
        header.apply {
            button1.setImageResource(R.drawable.ic_arrow_left)
            button2.hide()
            button3.hide()
            button4.hide()
            textTitle.text = getString(R.string.time_format)
        }
    }

    private fun setupEvents() = with(viewBinding) {
        header.button1.setOnClickListener {
            finish()
        }
    }
}
