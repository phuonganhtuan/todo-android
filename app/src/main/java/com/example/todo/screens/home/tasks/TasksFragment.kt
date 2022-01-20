package com.example.todo.screens.home.tasks

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.todo.R
import com.example.todo.base.BaseFragment
import com.example.todo.databinding.FragmentTasksBinding
import com.example.todo.utils.gone
import com.example.todo.utils.hide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : BaseFragment<FragmentTasksBinding>() {

    private val viewModel: TasksViewModel by viewModels()

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentTasksBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        initViews()
        initData()
        setupEvents()
        observeData()
    }

    private fun setupToolbar() = with(viewBinding.layoutTop) {
        button1.setImageResource(R.drawable.ic_setting)
        button4.setImageResource(R.drawable.ic_search)
        button2.hide()
        button3.hide()
    }

    private fun initViews() = with(viewBinding) {

    }

    private fun initData() = with(viewModel) {

    }

    private fun setupEvents() = with(viewBinding) {
        layoutTop.button1.setOnClickListener { toSetting() }
        layoutTop.button4.setOnClickListener { toSearch() }
        buttonNewTask.setOnClickListener { toNewTask() }
    }

    private fun observeData() = with(viewModel) {

    }

    private fun toSetting() {

    }

    private fun toSearch() {

    }

    private fun toNewTask() {

    }
}
