package com.trustedapp.todolist.planner.reminders.screens.home.mine

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.trustedapp.todolist.planner.reminders.base.BaseFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentMineBinding
import com.trustedapp.todolist.planner.reminders.screens.taskdetail.TaskDetailActivity
import com.trustedapp.todolist.planner.reminders.utils.Constants
import com.trustedapp.todolist.planner.reminders.utils.FirebaseLog
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class MineFragment : BaseFragment<FragmentMineBinding>() {

    private val viewModel: MineViewModel by viewModels()

    @Inject
    lateinit var chartAdapter: CatStatisticAdapter

    @Inject
    lateinit var taskAdapter: MineTaskAdapter

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMineBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupEvents()
        observeData()
        FirebaseLog.logEventMineScreen()
    }

    private fun initViews() = with(viewBinding) {
        recyclerNext7Days.adapter = taskAdapter
        recyclerChart.adapter = chartAdapter
    }

    private fun setupEvents() = with(viewBinding) {
        taskAdapter.onTaskClickListener = {
            startActivity(Intent(requireContext(), TaskDetailActivity::class.java).apply {
                putExtra(Constants.KEY_TASK_ID, it)
            })
        }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                next7DaysTasks.collect {
                    taskAdapter.submitList(it)
                    if (it.isEmpty()) {
                        viewBinding.imageNoTask.show()
                    } else {
                        viewBinding.imageNoTask.gone()
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                completedTasksCount.collect {
                    viewBinding.textDoneNum.text = it.toString()
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                pendingTasksCount.collect {
                    viewBinding.textPendingNum.text = it.toString()
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                chartData.collect {
                    chartAdapter.submitList(it)
                    viewBinding.chart.setData(it.map { item -> item.percent })
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                tasksCount.collect {
                    viewBinding.apply {
                        if (it == 0) {
                            chart.gone()
                            recyclerChart.gone()
                            viewIndicatorBottom.gone()
                        } else {
                            chart.show()
                            recyclerChart.show()
                            viewIndicatorBottom.show()
                        }
                    }
                }
            }
        }
    }
}
