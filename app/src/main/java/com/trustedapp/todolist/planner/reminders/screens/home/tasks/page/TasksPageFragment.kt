package com.trustedapp.todolist.planner.reminders.screens.home.tasks.page

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseFragment
import com.trustedapp.todolist.planner.reminders.data.models.model.TaskPageType
import com.trustedapp.todolist.planner.reminders.databinding.FragmentTasksPageBinding
import com.trustedapp.todolist.planner.reminders.screens.taskdetail.TaskDetailActivity
import com.trustedapp.todolist.planner.reminders.utils.Constants.KEY_TASK_ID
import com.trustedapp.todolist.planner.reminders.utils.getStringByLocale
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TasksPageFragment : BaseFragment<FragmentTasksPageBinding>() {

    private val viewModel: TasksPageViewModel by viewModels()

    private var type = TaskPageType.TODAY

    @Inject
    lateinit var adapter: TaskAdapter

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentTasksPageBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initViews()
        observeData()
        setupEvents()
    }

    override fun onStart() {
        super.onStart()
        adapter.notifyDataSetChanged()
    }

    private fun initViews() = with(viewBinding) {
        recyclerTasks.adapter = adapter
        if (viewModel.type == TaskPageType.TODAY) {
            adapter.isHideDay = true
        }
    }

    private fun initData() {
        if (viewModel.type == null) viewModel.type = type
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                when (type) {
                    TaskPageType.TODAY -> {
                        todayTasks.collect {
                            adapter.submitList(it)
                            viewBinding.textTaskCount.text =
                                "${requireContext().getStringByLocale(R.string.today_task)} (${it.count()})"
                            showOrHideNoTask(it.isNullOrEmpty())
                        }
                    }
                    TaskPageType.FUTURE -> {
                        futureTasks.collect {
                            adapter.submitList(it)
                            viewBinding.textTaskCount.text =
                                "${requireContext().getStringByLocale(R.string.future_task)} (${it.count()})"
                            showOrHideNoTask(it.isNullOrEmpty())
                        }
                    }
                    TaskPageType.DONE -> {
                        doneTasks.collect {
                            adapter.submitList(it)
                            viewBinding.textTaskCount.text =
                                "${requireContext().getStringByLocale(R.string.done_task)} (${it.count()})"
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
