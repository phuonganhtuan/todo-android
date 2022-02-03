package com.example.todo.screens.newtask.calendar

import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.todo.base.BaseBottomSheetDialogFragment
import com.example.todo.databinding.LayoutAddCalendarBinding
import com.example.todo.utils.DateTimeUtils
import kotlinx.coroutines.flow.*
import java.util.*

import android.widget.TextView
import com.example.todo.screens.newtask.*


class AddCalendarBottomSheetDialogFragment :
    BaseBottomSheetDialogFragment<LayoutAddCalendarBinding>() {

    private val viewModel: NewTaskViewModel by activityViewModels()

    private lateinit var mTimePicker: TimePickerDialog
    private lateinit var mSetReminderDialog: SetReminderDialog
    private lateinit var mSetRepeatDialog: SetRepeatAtDialog

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
        mTimePicker = TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                onTimeSet(timePicker, hour, minute)
            },
            slHour,
            slMinute,
            false
        )
        mSetReminderDialog = SetReminderDialog()

        mSetRepeatDialog = SetRepeatAtDialog()
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
                    .zip(selectedMinute.filter { it > -1 }) { hour, minute ->
                        val hourValue = when (hour > 9) {
                            true -> "$hour"
                            else -> "0$hour"
                        }
                        val minuteValue = when (minute > 9) {
                            true -> "$minute"
                            else -> ")$minute"
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
                viewModel.selectedReminderTime.filter { it != ReminderTimeEnum.NONE && viewModel.isCheckedReminder.value }
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
                    viewBinding.swReminder.isChecked = it
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedRepeatAt.filter { it != RepeatAtEnum.NONE && viewModel.isCheckedRepeat.value }
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
                    viewBinding.swRepeat.isChecked = it
                }
            }
        }
    }

    private fun onDateSelected(date: Date) = with(viewBinding) {
        viewModel.selectDate(date)
    }

    private fun onClickAddTime() {
        mTimePicker.show()
    }

    private fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) = with(viewBinding) {
        viewModel.selectHourAndMinute(hourOfDay, minute)
    }

    private fun onCheckChangeReminder() = with(viewBinding) {
        if (swReminder.isChecked) {
            viewModel.onCheckChangeReminder(false)
            onClickReminder(swReminder)
        } else {
            viewModel.resetReminderDefault()
        }
    }

    private fun onClickReminder(view: View) {
        if (mSetReminderDialog.isAdded()) {
            return; //or return false/true, based on where you are calling from
        }
        mSetReminderDialog.show(childFragmentManager, "Open Reminder Dialog")
    }

    private fun onCheckChangeRepeat() = with(viewBinding) {
        if (swRepeat.isChecked) {
            viewModel.onCheckChangeRepeat(false)
            onClickRepeat(swRepeat)
        } else {
            viewModel.resetRepeatDefaul()
        }
    }

    private fun onClickRepeat(view: View) {
        if (mSetRepeatDialog.isAdded()) {
            return; //or return false/true, based on where you are calling from
        }
        mSetRepeatDialog.show(childFragmentManager, "Open Repeat Dialog")
    }

}
