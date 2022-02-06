package com.example.todo.screens.newtask

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation.findNavController
import com.example.todo.R
import com.example.todo.base.BaseActivity
import com.example.todo.databinding.ActivityNewTaskBinding
import com.example.todo.databinding.ActivityTaskDetailBinding
import com.example.todo.utils.hide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class NewTaskActivity : BaseActivity<ActivityNewTaskBinding>() {

    override fun inflateViewBinding() = ActivityNewTaskBinding.inflate(layoutInflater)

    private val viewModel: NewTaskViewModel by viewModels()

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {
        setupToolbar()
        setupEvents()
        observeData()
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

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isAdded.collect {
                    if (it) {
                        showToastMessage(getString(R.string.added_task))
                        finish()
                    }
                }
            }
        }
    }
}