package com.trustedapp.todolist.planner.reminders.screens.newtask.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDialogFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentSetCustomReminderTimeBinding
import com.trustedapp.todolist.planner.reminders.screens.newtask.NewTaskViewModel

class SetCustomReminderTime: BaseDialogFragment<FragmentSetCustomReminderTimeBinding>() {
    private val viewModel: NewTaskViewModel by activityViewModels()

    companion object {
        private const val MINUTE_UNIT_INDEX = 0
        private const val HOUR_UNIT_INDEX = 1
        private const val DAY_UNIT_INDEX = 2
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
    }

    private fun initView() = with(viewBinding){
        npRepeatTimeValue.wrapSelectorWheel = false
        npRepeatTimeUnit.wrapSelectorWheel = false
        npRepeatTimeValue.minValue = list[0]
        npRepeatTimeValue.maxValue = list[list.count() - 1]


        val reminderTimeUnits = resources.getStringArray(R.array.repeat_time_unit)
        npRepeatTimeUnit.minValue = 0
        npRepeatTimeUnit.maxValue = reminderTimeUnits.count() - 1Af
        npRepeatTimeUnit.displayedValues = reminderTimeUnits
    }

    private fun initData() = with(viewBinding){

    }

}