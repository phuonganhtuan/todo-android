package com.trustedapp.todolist.planner.reminders.screens.newtask.calendar

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDialogFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentSetCustomReminderTimeBinding
import com.trustedapp.todolist.planner.reminders.screens.newtask.NewTaskViewModel
import java.text.SimpleDateFormat
import java.util.*

class SetCustomReminderTime: BaseDialogFragment<FragmentSetCustomReminderTimeBinding>() {
    private val viewModel: NewTaskViewModel by activityViewModels()

    companion object {
        private const val MINUTE_UNIT_INDEX = 0
        private const val HOUR_UNIT_INDEX = 1
        private const val DAY_UNIT_INDEX = 2
        private const val WEEK_UNIT_INDEX = 3
    }

    private var list: List<Int> = (1..100).toList()

    private var slUnitIndex: Int = 0
    private var slValueIndex: Int = 0


    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )= FragmentSetCustomReminderTimeBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        setEvents()
    }

    private fun initView() = with(viewBinding){
        npRepeatTimeValue.wrapSelectorWheel = false
        npRepeatTimeUnit.wrapSelectorWheel = false
        npRepeatTimeValue.minValue = list[0]
        npRepeatTimeValue.maxValue = list[list.count() - 1]


        val reminderTimeUnits = resources.getStringArray(R.array.repeat_time_unit)
        npRepeatTimeUnit.minValue = 0
        npRepeatTimeUnit.maxValue = reminderTimeUnits.count() - 1
        npRepeatTimeUnit.displayedValues = reminderTimeUnits
    }

    private fun initData() = with(viewBinding){
        onChangeNumberPicker()
    }

    private fun setEvents() = with(viewBinding){
        npRepeatTimeValue.setOnValueChangedListener { numberPicker, oldValue, newValue ->
            slValueIndex = newValue
            onChangeNumberPicker()
        }

        npRepeatTimeUnit.setOnValueChangedListener { numberPicker, oldValue, newValue ->
            slUnitIndex = newValue
            onChangeNumberPicker()
        }
    }

    private fun onChangeNumberPicker() = with(viewBinding){
        val day = viewModel.selectedDate.value
        val hour = viewModel.selectedHour.value
        val minute = viewModel.selectedMinute.value

        val offset = list[slValueIndex] * when(slUnitIndex){
            MINUTE_UNIT_INDEX -> 60000
            HOUR_UNIT_INDEX -> 3600000
            DAY_UNIT_INDEX -> 86400000
            else -> 604800000
        }

        val calendar = Calendar.getInstance()
        calendar.time = day
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val reminderTime = SimpleDateFormat("dd/MM/yyyy hh:mm a").format(calendar.timeInMillis - offset)
        tvReminderTime.text = reminderTime
    }
}