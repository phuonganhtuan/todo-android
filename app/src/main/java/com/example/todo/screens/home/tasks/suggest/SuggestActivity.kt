package com.example.todo.screens.home.tasks.suggest

import android.content.Intent
import android.os.Bundle
import com.example.todo.base.BaseActivity
import com.example.todo.databinding.ActivitySuggestCreateTaskBinding
import com.example.todo.screens.newtask.NewTaskActivity
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
