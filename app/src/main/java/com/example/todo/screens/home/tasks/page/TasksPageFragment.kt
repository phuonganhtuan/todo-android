package com.example.todo.screens.home.tasks.page

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.todo.base.BaseFragment
import com.example.todo.data.models.entity.TaskEntity
import com.example.todo.data.models.model.TaskPageType
import com.example.todo.databinding.FragmentTasksPageBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TasksPageFragment : BaseFragment<FragmentTasksPageBinding>() {

    private var type: TaskPageType = TaskPageType.TODAY

    private val viewModel: TasksPageViewModel by viewModels()

    @Inject
    lateinit var adapter: TaskAdapter

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentTasksPageBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initData()
        observeData()
        setupEvents()
    }

    private fun initViews() = with(viewBinding) {
        recyclerTasks.adapter = adapter
    }

    private fun initData() {
        // Demo data
        adapter.submitList(
            listOf(
                TaskEntity(0, ""),
                TaskEntity(1, ""),
                TaskEntity(2, ""),
                TaskEntity(3, ""),
                TaskEntity(4, ""),
                TaskEntity(5, "")
            )
        )
    }

    private fun observeData() = with(viewModel) {

    }

    private fun setupEvents() = with(viewBinding) {

    }

    companion object {
        fun newInstance(taskType: TaskPageType) = TasksPageFragment().apply {
            type = taskType
        }
    }
}
