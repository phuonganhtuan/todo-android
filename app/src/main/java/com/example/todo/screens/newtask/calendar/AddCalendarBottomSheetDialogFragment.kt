package com.example.todo.screens.newtask.calendar

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.todo.R
import com.example.todo.base.BaseBottomSheetDialogFragment
import com.example.todo.databinding.LayoutAddCalendarBinding
import com.example.todo.screens.newtask.NewTaskViewModel
import com.example.todo.screens.newtask.ReminderTimeEnum
import com.example.todo.screens.newtask.RepeatAtEnum
import com.example.todo.utils.DateTimeUtils
import com.example.todo.utils.gone
import com.example.todo.utils.show
import com.google.android.material.timepicker.MaterialTimePicker
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import java.util.*
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE


class AddCalendarBottomSheetDialogFragment :
    BaseBottomSheetDialogFragment<LayoutAddCalendarBinding>() {

    private val viewModel: NewTaskViewModel by activityViewModels()

    private lateinit var timePicker: MaterialTimePicker

    val mCurrentTime = Calendar.getInstance()

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LayoutAddCalendarBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        setupEvent()
        observeData()
    }

    private fun initView() = with(viewBinding) {
        val slHour = when (viewModel.selectedHour.value > -1) {
            true -> viewModel.selectedHour.value
            else -> mCurrentTime.get(Calendar.HOUR_OF_DAY)
        }
        val slMinute = when (viewModel.selectedMinute.value > -1) {
            true -> viewModel.selectedMinute.value
            else -> mCurrentTime.get(Calendar.MINUTE)
        }
        // TimePicker
        timePicker = MaterialTimePicker
            .Builder()
            .setHour(slHour)
            .setMinute(slMinute)
            .build()
        tvRepeatValue.gone()
        tvReminderValue.gone()

        if (viewModel.selectedHour.value == -1 || viewModel.selectedMinute.value == -1) {
            val now = Calendar.getInstance()
            onTimeSet(now.get(HOUR_OF_DAY), now.get(MINUTE))
        }
    }

    private fun initData() = with(viewBinding) {
        tvSelectedDate.setText(
            DateTimeUtils.getComparableDateString(viewModel.selectedDate.value)
        )
    }

    private fun setupEvent() = with(viewBinding) {
        // SelectDate event
        calendarView.onDateSelectedListener = { onDateSelected(it) }

        // AddTime
        tvAddTime.setOnClickListener { onClickAddTime() }
        imgAddTime.setOnClickListener { onClickAddTime() }

        // Reminder
        imgReminder.setOnClickListener { onClickReminder(it) }
        tvReminder.setOnClickListener { onClickReminder(it) }
        swReminder.setOnClickListener { onCheckChangeReminder() }

        // Repeat
        imgRepeat.setOnClickListener { onClickRepeat(it) }
        tvRepeat.setOnClickListener { onClickRepeat(it) }
        swRepeat.setOnClickListener { onCheckChangeRepeat() }

        timePicker.addOnPositiveButtonClickListener {
            onTimeSet(timePicker.hour, timePicker.minute)
        }
        layoutRoot.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectedDate.collect {
                    viewBinding.tvSelectedDate.text = DateTimeUtils.getComparableDateString(it)
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectedHour.filter { it > -1 }
                    .combine(selectedMinute.filter { it > -1 }) { hour, minute ->
                        val hourValue = when (hour > 9) {
                            true -> "$hour"
                            else -> "0$hour"
                        }
                        val minuteValue = when (minute > 9) {
                            true -> "$minute"
                            else -> "0$minute"
                        }
                        "$hourValue:$minuteValue"
                    }.collect {
                        Log.e("observeData: ", it)
                        viewBinding.tvAddTime.setText(it)
                        viewBinding.tvAddTime.setTextColor(Color.BLACK)
                    }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedReminderTime.filter { it != ReminderTimeEnum.NONE }
                    .collect {
                        viewBinding.tvReminderValue.setText(resources.getString(it.getStringid()))
                        viewBinding.tvReminderValue.setTextColor(Color.BLACK)
                        val layout: TextView = viewBinding.tvReminderValue
                        val params: ViewGroup.LayoutParams = layout.layoutParams
                        params.height = 100
                        viewBinding.tvReminderValue.layoutParams = params
                    }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isCheckedReminder.collect {
                    viewBinding.apply {
                        if (it) tvReminderValue.show()
                        swReminder.isChecked = it
                        if (!it) {
                            tvReminderValue.gone()
                            tvRepeatValue.gone()
                            swRepeat.isChecked = false
                            viewModel.resetRepeatDefault()
                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedRepeatAt.filter { it != RepeatAtEnum.NONE }
                    .collect {
                        viewBinding.tvRepeatValue.setText(resources.getString(it.getStringid()))
                        viewBinding.tvRepeatValue.setTextColor(Color.BLACK)
                        val layout: TextView = viewBinding.tvRepeatValue
                        val params: ViewGroup.LayoutParams = layout.layoutParams
                        params.height = 100
                        viewBinding.tvRepeatValue.layoutParams = params
                    }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isCheckedRepeat.collect {
                    if (isCheckedReminder.value) {
                        viewBinding.apply {
                            if (it) tvRepeatValue.show()
                            swRepeat.isChecked = it
                        }
                    }
                }
            }
        }
    }

    private fun onDateSelected(date: Date) = with(viewBinding) {
        viewModel.selectDate(date)
    }

    private fun onClickAddTime() {
        activity?.supportFragmentManager?.let {
            timePicker.show(it, MaterialTimePicker::class.java.simpleName)
        }
    }

    private fun onTimeSet(hourOfDay: Int, minute: Int) = with(viewBinding) {
        viewModel.selectHourAndMinute(hourOfDay, minute)
    }

    private fun onCheckChangeReminder() = with(viewBinding) {
        if (swReminder.isChecked) {
            viewModel.onCheckChangeReminder(false)
            onClickReminder(swReminder)
        } else {
            tvReminderValue.gone()
            viewModel.resetReminderDefault()
        }
    }

    private fun onClickReminder(view: View) {
        findNavController().navigate(R.id.toAddReminder)
    }

    private fun onCheckChangeRepeat() = with(viewBinding) {
        if (swRepeat.isChecked) {
            if (!viewModel.isCheckedReminder.value) {
                swRepeat.isChecked = false
                tvRepeatValue.gone()
                viewModel.resetRepeatDefault()
                return@with
            }
            viewModel.onCheckChangeRepeat(false)
            onClickRepeat(swRepeat)
        } else {
            tvRepeatValue.gone()
            viewModel.resetRepeatDefault()
        }
    }

    private fun onClickRepeat(view: View) {
        findNavController().navigate(R.id.toAddRepeat)
    }
}
