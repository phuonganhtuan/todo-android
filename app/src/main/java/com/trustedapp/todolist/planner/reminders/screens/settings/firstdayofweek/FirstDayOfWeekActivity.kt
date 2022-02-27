package com.trustedapp.todolist.planner.reminders.screens.settings.firstdayofweek

import android.os.Bundle
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.databinding.ActivityFirstDayOfWeekBinding
import com.trustedapp.todolist.planner.reminders.utils.hide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FirstDayOfWeekActivity: BaseActivity<ActivityFirstDayOfWeekBinding>() {

    override fun inflateViewBinding() = ActivityFirstDayOfWeekBinding.inflate(layoutInflater)

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
            textTitle.text = getString(R.string.first_day_of_week)
        }
    }

    private fun setupEvents() = with(viewBinding) {
        header.button1.setOnClickListener {
            finish()
        }
    }
}
