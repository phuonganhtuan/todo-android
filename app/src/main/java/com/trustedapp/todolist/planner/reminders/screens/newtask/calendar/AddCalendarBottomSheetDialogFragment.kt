package com.trustedapp.todolist.planner.reminders.screens.newtask.calendar

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseBottomSheetDialogFragment
import com.trustedapp.todolist.planner.reminders.databinding.LayoutAddCalendarBinding
import com.trustedapp.todolist.planner.reminders.screens.newtask.NewTaskViewModel
import com.trustedapp.todolist.planner.reminders.screens.newtask.ReminderTimeEnum
import com.trustedapp.todolist.planner.reminders.screens.newtask.RepeatAtEnum
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import com.google.android.material.timepicker.MaterialTimePicker
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.isUserInteractionEnabled
import com.trustedapp.todolist.planner.reminders.utils.show
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

    private var isFirstTime = true

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
//            val now = Calendar.getInstance()
//            onTimeSet(now.get(HOUR_OF_DAY), now.get(MINUTE))
            lnReminder.isUserInteractionEnabled(false)
            lnReminder.alpha = 0.5f
        }
    }

    private fun initData() = with(viewBinding) {
        viewModel._hasTime.value = true
        if (viewModel.selectedDate.value == null) {
            tvSelectedDate.text = DateTimeUtils.getComparableDateString(Calendar.getInstance().time)
        } else {
            tvSelectedDate.text = DateTimeUtils.getComparableDateString(viewModel.selectedDate.value)
        }
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
                    viewBinding.apply {
                        if (it != null) {
                            tvSelectedDate.text = DateTimeUtils.getComparableDateString(it)
                            if (isFirstTime && DateTimeUtils.getComparableDateString(it) != DateTimeUtils.getComparableDateString(Calendar.getInstance().time)) {
                                calendarView.setDate(Calendar.getInstance().apply { time = it })
                                isFirstTime = false
                            }
                        }
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectedHour.filter { it > -1 }
                    .combine(selectedMinute.filter { it > -1 }) { hour, minute ->
                        val date = Calendar.getInstance().apply {
                            set(HOUR_OF_DAY, hour)
                            set(MINUTE, minute)
                        }
                        DateTimeUtils.getHourMinuteFromMillisecond(date.timeInMillis)
                    }.collect {
                        Log.e("observeData: ", it)
                        viewBinding.apply {
                            tvAddTime.text = it
                            tvAddTime.setTextColor(Color.BLACK)
                            lnReminder.isUserInteractionEnabled(true)
                            lnReminder.alpha = 1f
                        }
                    }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedReminderTime.filter { it != ReminderTimeEnum.NONE }
//                    .filter { viewModel.isCheckedReminder.value == true }
                    .collect {
                        val text =
                            if (it == ReminderTimeEnum.CUSTOM_DAY_BEFORE) "${customReminderTime.value.toString()} ${
                                resources.getString(customReminderTimeUnit.value.getStringid())
                                    .lowercase()
                            } ${
                                resources.getString(R.string.before).lowercase()
                            }" else resources.getString(it.getStringid())
                        viewBinding.apply {
                            tvReminderValue.text = text
                            tvReminderValue.setTextColor(Color.BLACK)
                            val layout: TextView = tvReminderValue
                            val params: ViewGroup.LayoutParams = layout.layoutParams
                            params.height = 100
                            tvReminderValue.layoutParams = params
                        }

                    }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isCheckedReminder.collect {
                    viewBinding.apply {
                        if (it) {
                            tvReminderValue.show()
                        }
                        swReminder.isChecked = it
                        if (!it) {
                            tvReminderValue.gone()
//                            tvRepeatValue.gone()
//                            viewModel.onCheckChangeRepeat(false)
//                            viewModel.resetRepeatDefault()

                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedRepeatAt.filter { it != RepeatAtEnum.NONE }
//                    .filter { viewModel.isCheckedRepeat.value == true }
                    .collect {
                        viewBinding.apply {
                            tvRepeatValue.setText(resources.getString(it.getStringid()))
                            tvRepeatValue.setTextColor(Color.BLACK)
                            val layout: TextView = tvRepeatValue
                            val params: ViewGroup.LayoutParams = layout.layoutParams
                            params.height = 100
                            tvRepeatValue.layoutParams = params
                        }
                    }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isCheckedRepeat.collect {
                    viewBinding.apply {
                        if (it) {
                            tvRepeatValue.show()
                        }
                        swRepeat.isChecked = it
                        if (!it) {
                            tvRepeatValue.gone()
//                            tvRepeatValue.gone()
//                            viewModel.onCheckChangeRepeat(false)
//                            viewModel.resetRepeatDefault()

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
            swReminder.isChecked = false
            viewModel.onCheckChangeReminder(false)
            onClickReminder(swReminder)
        } else {
            tvReminderValue.gone()
            viewModel.onCheckChangeReminder(false)
//            viewModel.resetReminderDefault()
        }
    }

    private fun onClickReminder(view: View) {
        findNavController().navigate(R.id.toAddReminder)
    }

    private fun onCheckChangeRepeat() = with(viewBinding) {
        if (swRepeat.isChecked) {
//            if (!viewModel.isCheckedReminder.value) {
//                swRepeat.isChecked = false
//                tvRepeatValue.gone()
////                viewModel.resetRepeatDefault()
//                return@with
//            }
            swRepeat.isChecked = false
            viewModel.onCheckChangeRepeat(false)
            onClickRepeat(swRepeat)
        } else {
            tvRepeatValue.gone()
            viewModel.onCheckChangeRepeat(false)
//            viewModel.resetRepeatDefault()
        }
    }

    private fun onClickRepeat(view: View) {
        findNavController().navigate(R.id.toAddRepeat)
    }
}
