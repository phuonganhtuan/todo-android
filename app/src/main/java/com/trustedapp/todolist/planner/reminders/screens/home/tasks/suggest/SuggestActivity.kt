package com.trustedapp.todolist.planner.reminders.screens.home.tasks.suggest

import android.content.Intent
import android.os.Bundle
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.databinding.ActivitySuggestCreateTaskBinding
import com.trustedapp.todolist.planner.reminders.screens.newtask.NewTaskActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuggestActivity : BaseActivity<ActivitySuggestCreateTaskBinding>() {

    override fun inflateViewBinding() = ActivitySuggestCreateTaskBinding.inflate(layoutInflater)

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {
        setupEvents()
    }

    private fun setupEvents() = with(viewBinding) {
        buttonCreateTask.setOnClickListener {
            startActivity(Intent(this@SuggestActivity, NewTaskActivity::class.java))
            finish()
        }
        layoutRoot.setOnClickListener {
            finish()
        }
        layoutSuggest.setOnClickListener { }
    }
}
