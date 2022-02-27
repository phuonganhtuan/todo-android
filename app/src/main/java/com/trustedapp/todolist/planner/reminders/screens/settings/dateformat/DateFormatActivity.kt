package com.trustedapp.todolist.planner.reminders.screens.settings.dateformat

import android.os.Bundle
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.databinding.ActivityDateFormatBinding
import com.trustedapp.todolist.planner.reminders.databinding.ActivityTimeFormatBinding
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import com.trustedapp.todolist.planner.reminders.utils.hide
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DateFormatActivity: BaseActivity<ActivityDateFormatBinding>() {

    override fun inflateViewBinding() = ActivityDateFormatBinding.inflate(layoutInflater)

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
            textTitle.text = getString(R.string.date_format)
        }
        radioButtonType1.text = DateTimeUtils.getComparableDateString(Calendar.getInstance().time, DateTimeUtils.DATE_FORMAT_TYPE_1)
        radioButtonType2.text = DateTimeUtils.getComparableDateString(Calendar.getInstance().time, DateTimeUtils.DATE_FORMAT_TYPE_2)
        radioButtonType3.text = DateTimeUtils.getComparableDateString(Calendar.getInstance().time, DateTimeUtils.DATE_FORMAT_TYPE_3)
    }

    private fun setupEvents() = with(viewBinding) {
        header.button1.setOnClickListener {
            finish()
        }
    }
}
