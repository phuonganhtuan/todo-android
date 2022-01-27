package com.example.todo.screens.newtask.calendar

import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.coordinatorlayout.R
import androidx.fragment.app.activityViewModels
import com.example.todo.base.BaseBottomSheetDialogFragment
import com.example.todo.common.calendarview.TaskCalendarView
import com.example.todo.databinding.LayoutAddCalendarBinding
import com.example.todo.screens.newtask.NewTaskViewModel
import com.example.todo.utils.DateTimeUtils
import java.util.*
import kotlin.math.log

class AddCalendarBottomSheetDialogFragment :
    BaseBottomSheetDialogFragment<LayoutAddCalendarBinding>() {

    private val viewModel: NewTaskViewModel by activityViewModels()

    private lateinit var mTimePicker: TimePickerDialog
    private lateinit var mSetReminderDialog: SetReminderDialog
    private lateinit var mSetRepeatDialog: SetRepeatAtDialog

    val mCurrentTime = Calendar.getInstance()
    private var selDate: Date = mCurrentTime.time
    private var selHour: Int = mCurrentTime.get(Calendar.HOUR_OF_DAY)
    private var selMinute: Int = mCurrentTime.get(Calendar.MINUTE)

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LayoutAddCalendarBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        setupEvent()
    }

    private fun initView() = with(viewBinding) {
        // TimePicker
        mTimePicker = TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                onTimeSet(timePicker, hour, minute)
            },
            selHour,
            selMinute,
            false
        )
        mSetReminderDialog = SetReminderDialog()

        mSetRepeatDialog = SetRepeatAtDialog()
    }

    private fun initData() = with(viewBinding) {
        tvSelectedDate.setText(
            DateTimeUtils.getComparableDateString(selDate)
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

        // Repeat
        imgRepeat.setOnClickListener { onClickRepeat(it) }
        tvRepeat.setOnClickListener { onClickRepeat(it) }
    }

    private fun onDateSelected(date: Date)= with(viewBinding) {
        selDate = date
        tvSelectedDate.setText(
            DateTimeUtils.getComparableDateString(selDate)
        )
    }

    private fun onClickAddTime() {
        mTimePicker.show()
    }

    private fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int)= with(viewBinding) {
        selHour = hourOfDay
        selMinute = minute
        val time = "${selHour.toString()}:${selMinute.toString()}"
        tvAddTime.setText(time)
    }

    private fun onClickReminder(view: View){
        mSetReminderDialog.show(childFragmentManager, "Open Reminder Dialog")
    }

    private fun onClickRepeat(view: View){
        mSetRepeatDialog.show(childFragmentManager, "Open Repeat Dialog")
    }

}
