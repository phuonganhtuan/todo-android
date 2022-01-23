package com.example.todo.screens.home.tasks.page

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.todo.base.BaseFragment
import com.example.todo.data.models.model.TaskPageType
import com.example.todo.databinding.FragmentTasksPageBinding
import com.example.todo.screens.taskdetail.TaskDetailActivity
import com.example.todo.utils.Constants.KEY_TASK_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TasksPageFragment : BaseFragment<FragmentTasksPageBinding>() {

    private var type: TaskPageType = TaskPageType.TODAY

    private val viewModel: TasksPageViewModel by activityViewModels()

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

    }

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                when (type) {
                    TaskPageType.TODAY -> {
                        todayTasks.collect {
                            adapter.submitList(it)
                        }
                    }
                    TaskPageType.FUTURE -> {
                        futureTasks.collect {
                            adapter.submitList(it)
                        }
                    }
                    TaskPageType.DONE -> {
                        doneTasks.collect {
                            adapter.submitList(it)
                        }
                    }
                }
            }
        }
    }

    private fun setupEvents() = with(viewBinding) {
        adapter.setOnTaskListener(object : OnTaskInteract {
            override fun onItemClick(id: Int) {
                startActivity(Intent(requireContext(), TaskDetailActivity::class.java).apply {
                    putExtra(KEY_TASK_ID, id)
                })
            }

            override fun onMarkChange(id: Int) {
                viewModel.updateMark(id)
            }

            override fun onStatusChange(id: Int) {
                viewModel.updateStatus(id)
            }
        })
    }

    companion object {
        fun newInstance(taskType: TaskPageType) = TasksPageFragment().apply {
            type = taskType
        }
    }
}
