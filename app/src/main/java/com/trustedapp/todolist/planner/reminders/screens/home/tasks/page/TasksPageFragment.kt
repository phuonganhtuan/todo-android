package com.trustedapp.todolist.planner.reminders.screens.home.tasks.page

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.trustedapp.todolist.planner.reminders.base.BaseFragment
import com.trustedapp.todolist.planner.reminders.data.models.model.TaskPageType
import com.trustedapp.todolist.planner.reminders.databinding.FragmentTasksPageBinding
import com.trustedapp.todolist.planner.reminders.screens.taskdetail.TaskDetailActivity
import com.trustedapp.todolist.planner.reminders.utils.Constants.KEY_TASK_ID
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.show
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

    override fun onStart() {
        super.onStart()
        adapter.notifyDataSetChanged()
    }

    private fun initViews() = with(viewBinding) {
        recyclerTasks.adapter = adapter
        if (type == TaskPageType.TODAY) {
            adapter.isHideDay = true
        }
    }

    private fun initData() {

    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                when (type) {
                    TaskPageType.TODAY -> {
                        todayTasks.collect {
                            adapter.submitList(it)
                            viewBinding.apply {
                                textTaskCount.text = "Today task (${it.count()})"
                            }
                            showOrHideNoTask(it.isNullOrEmpty())
                        }
                    }
                    TaskPageType.FUTURE -> {
                        futureTasks.collect {
                            adapter.submitList(it)
                            viewBinding.textTaskCount.text = "Future task (${it.count()})"
                            showOrHideNoTask(it.isNullOrEmpty())
                        }
                    }
                    TaskPageType.DONE -> {
                        doneTasks.collect {
                            adapter.submitList(it)
                            viewBinding.textTaskCount.text = "Completed task (${it.count()})"
                            showOrHideNoTask(it.isNullOrEmpty())
                        }
                    }
                }
            }
        }
    }

    private fun showOrHideNoTask(isShow: Boolean) = with(viewBinding) {
        if (isShow) {
            imageNoTask.show()
            textTaskCount.gone()
        } else {
            imageNoTask.gone()
            textTaskCount.show()
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
                viewModel.updateStatus(requireContext(), id)
            }
        })
    }

    companion object {
        fun newInstance(taskType: TaskPageType) = TasksPageFragment().apply {
            type = taskType
        }
    }
}
