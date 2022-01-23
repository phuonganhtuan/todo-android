package com.example.todo.screens.taskdetail

import android.os.Bundle
import com.example.todo.R
import com.example.todo.base.BaseActivity
import com.example.todo.databinding.ActivityTaskDetailBinding
import com.example.todo.utils.hide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskDetailActivity: BaseActivity<ActivityTaskDetailBinding>() {

    override fun inflateViewBinding() = ActivityTaskDetailBinding.inflate(layoutInflater)

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {
        setupToolbar()
        setupEvents()
    }

    private fun setupToolbar() = with(viewBinding.layoutTop) {
        button1.setImageResource(R.drawable.ic_arrow_left)
        button4.hide()
        button2.hide()
        button3.hide()
    }

    private fun setupEvents() = with(viewBinding) {
        layoutTop.button1.setOnClickListener { finish() }
    }
}