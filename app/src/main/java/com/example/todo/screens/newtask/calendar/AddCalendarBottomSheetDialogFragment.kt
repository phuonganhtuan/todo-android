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
import com.example.todo.databinding.LayoutAddCalendarBinding
import com.example.todo.screens.newtask.NewTaskViewModel
import java.util.*
import kotlin.math.log

class AddCalendarBottomSheetDialogFragment :
    BaseBottomSheetDialogFragment<LayoutAddCalendarBinding>() {

    private val viewModel: NewTaskViewModel by activityViewModels()

    private lateinit var mTimePicker: TimePickerDialog
    val mCurrentTime = Calendar.getInstance()
    private var selHour:Int = mCurrentTime.get(Calendar.HOUR_OF_DAY)
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

    private fun initView() = with(viewBinding){
        // TimePicker
        mTimePicker = TimePickerDialog(context,TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            onTimeSet(timePicker, hour, minute)
        },selHour, selMinute, false )

    }

    private fun initData() = with(viewBinding){

    }

    private fun setupEvent() = with(viewBinding){
        viewBinding.tvAddTime.setOnClickListener { onClickAddTime() }
        viewBinding.imgAddTime.setOnClickListener { onClickAddTime() }
    }

    private fun onClickAddTime(){
        mTimePicker.show()
    }

    fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        selHour = hourOfDay
        selMinute = minute
        val time = "${selHour.toString()}:${selMinute.toString()}"
        viewBinding.tvAddTime.setText(time)
    }
}
