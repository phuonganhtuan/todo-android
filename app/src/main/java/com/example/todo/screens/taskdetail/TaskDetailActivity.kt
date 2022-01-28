package com.example.todo.screens.taskdetail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.todo.R
import com.example.todo.base.BaseActivity
import com.example.todo.databinding.ActivityTaskDetailBinding
import com.example.todo.utils.Constants
import com.example.todo.utils.hide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class TaskDetailActivity : BaseActivity<ActivityTaskDetailBinding>() {

    private val viewModel: TaskDetailViewModel by viewModels()

    override fun inflateViewBinding() = ActivityTaskDetailBinding.inflate(layoutInflater)

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {
        setupToolbar()
        initData()
        setupEvents()
        observeData()
    }

    private fun setupToolbar() = with(viewBinding.layoutTop) {
        button1.setImageResource(R.drawable.ic_arrow_left)
        button3.setImageResource(R.drawable.ic_edit)
        button4.setImageResource(R.drawable.ic_more)
        button2.hide()
    }

    private fun initData() {
        val taskId = intent?.extras?.getInt(Constants.KEY_TASK_ID)
        taskId ?: finish()
        viewModel.initData(taskId!!)
    }

    private fun setupEvents() = with(viewBinding) {
        layoutTop.button1.setOnClickListener { finish() }
        layoutTop.button3.setOnClickListener {
            if (viewModel.isEditing.value) viewModel.toViewMode() else viewModel.toEditMode()
        }
        layoutTop.button4.setOnClickListener {

        }
    }

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isEditing.collect {
                    val icon = if (it) R.drawable.ic_done_grey else R.drawable.ic_edit
                    viewBinding.layoutTop.button3.setImageResource(icon)
                }
            }
        }
    }
}