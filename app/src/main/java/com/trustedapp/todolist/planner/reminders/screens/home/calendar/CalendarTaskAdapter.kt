package com.trustedapp.todolist.planner.reminders.screens.home.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDiffCallBack
import com.trustedapp.todolist.planner.reminders.base.BaseViewHolder
import com.trustedapp.todolist.planner.reminders.data.models.model.DateModel
import com.trustedapp.todolist.planner.reminders.databinding.ItemCalendarTaskBinding
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.show
import java.util.*
import javax.inject.Inject

class CalendarTaskAdapter @Inject constructor() :
    androidx.recyclerview.widget.ListAdapter<DateModel, CalendarTaskViewHolder>(
        CalendarTaskDiffCallback()
    ) {

    var dateSelectListener: ((Date) -> Unit)? = null

    var selectedDate = Calendar.getInstance().time

    var isDark = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarTaskViewHolder {
        val itemViewBinding =
            ItemCalendarTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarTaskViewHolder(itemViewBinding, dateSelectListener)
    }

    override fun onBindViewHolder(holder: CalendarTaskViewHolder, position: Int) {
        holder.displayData(getItem(position), selectedDate, isDark)
    }
}

class CalendarTaskViewHolder(
    private val itemViewBinding: ItemCalendarTaskBinding,
    private val dateSelectListener: ((Date) -> Unit)?
) :
    BaseViewHolder<DateModel>(itemViewBinding) {

    private var date: Date? = null

    init {
        itemView.setOnClickListener {
            dateSelectListener?.let {
                it(date ?: Calendar.getInstance().time)
            }
        }
    }

    override fun displayData(entity: DateModel) = with(itemViewBinding) {

    }

    fun displayData(entity: DateModel, selectedDate: Date, isDark: Boolean) = with(itemViewBinding) {
        date = entity.date
        textDay.text =
            Calendar.getInstance().apply { time = entity.date }.get(Calendar.DAY_OF_MONTH)
                .toString()
        val isSelected =
            DateTimeUtils.getComparableDateString(entity.date) == DateTimeUtils.getComparableDateString(
                selectedDate
            )
        val textColor = if (isSelected) {
            R.color.white
        } else {
            if (isDark) {
                if (entity.isInMonth) R.color.white else R.color.color_cal_secondary
            } else {
                if (entity.isInMonth) R.color.color_cal_primary else R.color.color_cal_secondary
            }
        }
        textDay.setTextColor(ContextCompat.getColor(itemView.context, textColor))
        val isToday =
            DateTimeUtils.getComparableDateString(entity.date) == DateTimeUtils.getComparableDateString(
                Calendar.getInstance().time
            )
        val bgId = if (isSelected) R.drawable.bg_primary_rounded_8 else {
            if (isToday) R.drawable.bg_today_calendar_task else R.color.transparent
        }
        layoutDay.background = ContextCompat.getDrawable(itemView.context, bgId)
        if (entity.hasTask) viewHasTask.show() else viewHasTask.gone()
        if (entity.hasBirthday) imageBalloon.show() else imageBalloon.gone()
    }
}

class CalendarTaskDiffCallback : BaseDiffCallBack<DateModel>() {

    override fun areContentsTheSame(oldItem: DateModel, newItem: DateModel): Boolean =
        oldItem.id == newItem.id && oldItem.hasBirthday == newItem.hasBirthday &&
                oldItem.hasTask == newItem.hasTask && oldItem.isInMonth == newItem.isInMonth
}
