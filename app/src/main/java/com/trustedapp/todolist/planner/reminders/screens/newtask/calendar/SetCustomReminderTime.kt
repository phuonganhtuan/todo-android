package com.trustedapp.todolist.planner.reminders.screens.newtask.calendar

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDialogFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentSetCustomReminderTimeBinding
import com.trustedapp.todolist.planner.reminders.screens.newtask.CustomReminderTimeUnitEnum
import com.trustedapp.todolist.planner.reminders.screens.newtask.NewTaskViewModel
import java.text.SimpleDateFormat
import java.util.*

class SetCustomReminderTime : BaseDialogFragment<FragmentSetCustomReminderTimeBinding>() {
    private val viewModel: NewTaskViewModel by activityViewModels()

    private var list: List<Int> = (1..100).toList()

    private var slUnitIndex: Int = 0
    private var slValue: Int = 0


    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSetCustomReminderTimeBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        setEvents()
    }

    private fun initView() = with(viewBinding) {
        npRepeatTimeValue.wrapSelectorWheel = false
        npRepeatTimeUnit.wrapSelectorWheel = false
        npRepeatTimeValue.minValue = list[0]
        npRepeatTimeValue.maxValue = list[list.count() - 1]


        val reminderTimeUnits = resources.getStringArray(R.array.repeat_time_unit)
        npRepeatTimeUnit.minValue = 0
        npRepeatTimeUnit.maxValue = reminderTimeUnits.count() - 1
        npRepeatTimeUnit.displayedValues = reminderTimeUnits
    }

    private fun initData() = with(viewBinding) {
        if (viewModel.customReminderTime.value > 0 && list.contains(viewModel.customReminderTime.value)) {
            slValue = viewModel.customReminderTime.value
            npRepeatTimeValue.value = slValue
        }

        slUnitIndex = viewModel.customReminderTimeUnit.value.getIndex()
        npRepeatTimeUnit.value = slUnitIndex
        onChangeNumberPicker()
    }

    private fun setEvents() = with(viewBinding) {
        npRepeatTimeValue.setOnValueChangedListener { numberPicker, oldValue, newValue ->
            Log.e("setOnValueChangedListener - npRepeatTimeValue - newValue", newValue.toString())
            slValue = newValue
            onChangeNumberPicker()
        }

        npRepeatTimeUnit.setOnValueChangedListener { numberPicker, oldValue, newValue ->
            slUnitIndex = newValue
            onChangeNumberPicker()
        }

        btnCancel.setOnClickListener { dismiss() }
        btnDone.setOnClickListener { onClickDone() }
    }

    private fun onChangeNumberPicker() = with(viewBinding) {
        val day = viewModel.selectedDate.value
        val hour = viewModel.selectedHour.value
        val minute = viewModel.selectedMinute.value

        val offset = slValue * when (slUnitIndex) {
            CustomReminderTimeUnitEnum.MINUTE_UNIT.getIndex() -> CustomReminderTimeUnitEnum.MINUTE_UNIT.getOffset()
            CustomReminderTimeUnitEnum.HOUR_UNIT.getIndex() -> CustomReminderTimeUnitEnum.HOUR_UNIT.getOffset()
            CustomReminderTimeUnitEnum.DAY_UNIT.getIndex() -> CustomReminderTimeUnitEnum.DAY_UNIT.getOffset()
            else -> CustomReminderTimeUnitEnum.WEEK_UNIT.getOffset()
        }

        val calendar = Calendar.getInstance()
        calendar.time = day
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val reminderTime =
            SimpleDateFormat("dd/MM/yyyy hh:mm a").format(calendar.timeInMillis - offset)
        tvReminderTime.text = reminderTime
    }

    private fun onClickDone() = with(viewModel) {
        val time = slValue
        val unit = when (slUnitIndex) {
            CustomReminderTimeUnitEnum.MINUTE_UNIT.getIndex() -> CustomReminderTimeUnitEnum.MINUTE_UNIT
            CustomReminderTimeUnitEnum.HOUR_UNIT.getIndex() -> CustomReminderTimeUnitEnum.HOUR_UNIT
            CustomReminderTimeUnitEnum.DAY_UNIT.getIndex() -> CustomReminderTimeUnitEnum.DAY_UNIT
            else -> CustomReminderTimeUnitEnum.WEEK_UNIT
        }
        viewModel.setCustomReminderTime(time, unit)
        viewModel.setIsSelectCustomReminderTime(true)
        dismiss()
    }
}