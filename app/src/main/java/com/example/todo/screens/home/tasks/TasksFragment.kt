package com.example.todo.screens.home.tasks

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.todo.base.BaseFragment
import com.example.todo.databinding.FragmentTasksBinding
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
        initViews()
        initData()
        setupEvents()
        observeData()
    }

    private fun initViews() = with(viewBinding) {
    }

    private fun initData() = with(viewModel) {

    }

    private fun setupEvents() = with(viewBinding) {
    }

    private fun observeData() = with(viewModel) {
    }

}
