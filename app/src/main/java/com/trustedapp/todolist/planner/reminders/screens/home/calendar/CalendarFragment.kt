package com.trustedapp.todolist.planner.reminders.screens.home.calendar

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentCalendarBinding
import com.trustedapp.todolist.planner.reminders.screens.home.tasks.page.OnTaskInteract
import com.trustedapp.todolist.planner.reminders.screens.home.tasks.page.TaskAdapter
import com.trustedapp.todolist.planner.reminders.screens.taskdetail.TaskDetailActivity
import com.trustedapp.todolist.planner.reminders.setting.FirstDayOfWeek
import com.trustedapp.todolist.planner.reminders.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CalendarFragment : BaseFragment<FragmentCalendarBinding>() {

    private val viewModel: CalendarTaskViewModel by viewModels()

    @Inject
    lateinit var taskAdapter: TaskAdapter

    @Inject
    lateinit var calendarPageAdapter: CalendarPageAdapter

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
        FirebaseLog.logEventCalendarScreen()
    }

    override fun onResume() {
        super.onResume()
        calendarPageAdapter.selectedDate = viewModel.selectedDay.value
        viewModel.getTasks(calendarPageAdapter.selectedDate)
        calendarPageAdapter.notifyDataSetChanged()
    }

    private fun setupEvents() = with(viewBinding) {
        calendarPageAdapter.dateSelectListener = {
            viewModel.getTasks(it)
            calendarPageAdapter.selectedDate = it
            calendarPageAdapter.notifyDataSetChanged()
            Handler(Looper.getMainLooper()).post {
                when (DateTimeUtils.compareInMonth(
                    Calendar.getInstance().apply { time = it },
                    viewModel.selectingMonth.value
                )) {
                    1 -> layoutTop.button4.performClick()
                    -1 -> layoutTop.button1.performClick()
                }
            }
        }
        layoutTop.button1.setOnClickListener {
            try {
                Handler(Looper.getMainLooper()).post {
                    layoutCalendar.pagerCalendar.setCurrentItem(
                        layoutCalendar.pagerCalendar.currentItem - 1,
                        true
                    )
                }
            } catch (exception: Exception) {

            }
        }
        layoutTop.button4.setOnClickListener {
            try {
                Handler(Looper.getMainLooper()).post {
                    layoutCalendar.pagerCalendar.setCurrentItem(
                        layoutCalendar.pagerCalendar.currentItem + 1,
                        true
                    )
                }
            } catch (exception: Exception) {

            }
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
        layoutCalendar.pagerCalendar.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.setMonth(viewModel.months.value[position])
            }
        })
    }

    private fun initViews() = with(viewBinding) {
        layoutCalendar.pagerCalendar.adapter = calendarPageAdapter
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
        viewModel.setupMonths()
        val dayTitle = DateTimeUtils.getCalendarDayTitle()
        layoutCalendar.apply {
            textDay1.text = dayTitle[0]
            textDay2.text = dayTitle[1]
            textDay3.text = dayTitle[2]
            textDay4.text = dayTitle[3]
            textDay5.text = dayTitle[4]
            textDay6.text = dayTitle[5]
            textDay7.text = dayTitle[6]
        }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                selectingMonth.collect {
                    val index = viewModel.months.value.map { cal ->
                        DateTimeUtils.getComparableMonthString(cal.time)
                    }.indexOf(DateTimeUtils.getComparableMonthString(it.time))
                    if (viewBinding.layoutCalendar.pagerCalendar.currentItem != index) {
                        Handler(Looper.getMainLooper()).post {
                            viewBinding.layoutCalendar.pagerCalendar.setCurrentItem(index, false)
                        }
                    }
                    viewBinding.layoutTop.textTitle.text =
                        DateTimeUtils.getMonthYearString(it)
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
//                    calendarAdapter.selectedDate = it
//                    calendarAdapter.notifyDataSetChanged()
                    FirebaseLog.logEventCalendarInCalendarScreen()
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                months.collect {
                    calendarPageAdapter.listMonth = it
                    calendarPageAdapter.notifyDataSetChanged()
                    val index = it.map { cal -> DateTimeUtils.getComparableMonthString(cal.time) }
                        .indexOf(DateTimeUtils.getComparableMonthString(Calendar.getInstance().time))
                    viewBinding.layoutCalendar.pagerCalendar.setCurrentItem(index, false)
                }
            }
        }
    }
}
