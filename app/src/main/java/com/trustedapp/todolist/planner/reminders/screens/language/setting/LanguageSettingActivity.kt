package com.trustedapp.todolist.planner.reminders.screens.language.setting

import android.os.Bundle
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.databinding.ActivityLanguageSettingBinding
import com.trustedapp.todolist.planner.reminders.utils.hide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageSettingActivity: BaseActivity<ActivityLanguageSettingBinding>() {

    override fun inflateViewBinding() = ActivityLanguageSettingBinding.inflate(layoutInflater)

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {
        initViews()
        setupEvents()
    }

    private fun initViews() = with(viewBinding) {
        header.apply {
            button2.hide()
            button3.hide()
            button4.hide()
            button1.setBackgroundResource(R.drawable.ic_arrow_left)
            textTitle.text = getString(R.string.language)
        }
    }

    private fun setupEvents() = with(viewBinding) {
        header.button1.setOnClickListener {
            finish()
        }
    }
}
