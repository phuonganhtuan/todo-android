package com.trustedapp.todolist.planner.reminders.screens.home.calendar

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
import com.trustedapp.todolist.planner.reminders.databinding.FragmentCalendarBinding
import com.trustedapp.todolist.planner.reminders.screens.home.tasks.page.OnTaskInteract
import com.trustedapp.todolist.planner.reminders.screens.home.tasks.page.TaskAdapter
import com.trustedapp.todolist.planner.reminders.screens.taskdetail.TaskDetailActivity
import com.trustedapp.todolist.planner.reminders.utils.Constants
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class CalendarFragment : BaseFragment<FragmentCalendarBinding>() {

    private val viewModel: CalendarTaskViewModel by viewModels()

    @Inject
    lateinit var calendarAdapter: CalendarTaskAdapter

    @Inject
    lateinit var taskAdapter: TaskAdapter

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentCalendarBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initData()
        setupEvents()
        observeData()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getTasks(calendarAdapter.selectedDate)
    }

    private fun setupEvents() = with(viewBinding) {
        calendarAdapter.dateSelectListener = {
            viewModel.getTasks(it)
        }
        layoutTop.button1.setOnClickListener {
            viewModel.previousMonth()
        }
        layoutTop.button4.setOnClickListener {
            viewModel.nextMonth()
        }
        taskAdapter.setOnTaskListener(object : OnTaskInteract {
            override fun onItemClick(id: Int) {
                startActivity(Intent(requireContext(), TaskDetailActivity::class.java).apply {
                    putExtra(Constants.KEY_TASK_ID, id)
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

    private fun initViews() = with(viewBinding) {
        layoutCalendar.recyclerCalendar.adapter = calendarAdapter
        recyclerTasks.adapter = taskAdapter
        layoutTop.apply {
            button2.gone()
            button3.gone()
            button1.setImageResource(R.drawable.ic_previous)
            button4.setImageResource(R.drawable.ic_next)
        }
        taskAdapter.isHideDay = true
    }

    private fun initData() = with(viewBinding) {
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                selectingMonth.collect {
                    setupData()
                    viewBinding.layoutTop.textTitle.text =
                        DateTimeUtils.getMonthYearString(requireContext(), it)
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                days.collect {
                    if (it.isNotEmpty()) calendarAdapter.submitList(it)
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                tasks.collect {
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
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                selectedDay.collect {
                    calendarAdapter.selectedDate = it
                    calendarAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}
