package com.trustedapp.todolist.planner.reminders.common.calendarview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDiffCallBack
import com.trustedapp.todolist.planner.reminders.base.BaseViewHolder
import com.trustedapp.todolist.planner.reminders.data.models.model.DateModel
import com.trustedapp.todolist.planner.reminders.databinding.ItemCalendarDayBinding
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import java.util.*
import java.util.Calendar.DAY_OF_MONTH
import javax.inject.Inject

class CalendarAdapter @Inject constructor() :
    androidx.recyclerview.widget.ListAdapter<DateModel, CalendarViewHolder>(CalendarDiffCallback()) {

    var dateSelectListener: ((Date) -> Unit)? = null

    var selectedDate = Calendar.getInstance().time

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val itemViewBinding =
            ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(itemViewBinding, dateSelectListener)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.displayData(getItem(position), selectedDate)
    }
}

class CalendarViewHolder(
    private val itemViewBinding: ItemCalendarDayBinding,
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

    fun displayData(entity: DateModel, selectedDate: Date) = with(itemViewBinding) {
        date = entity.date
        textDay.text =
            Calendar.getInstance().apply { time = entity.date }.get(DAY_OF_MONTH).toString()
        val isSelected = DateTimeUtils.getComparableDateString(entity.date) == DateTimeUtils.getComparableDateString(selectedDate)
        val textColor = if (isSelected) {
            R.color.white
        } else {
            if (entity.isInMonth) R.color.color_cal_primary else R.color.color_cal_secondary
        }
        textDay.setTextColor(ContextCompat.getColor(itemView.context, textColor))
        val bgId =
            if (isSelected) R.drawable.bg_primary_rounded_8 else R.drawable.bg_white_rounded
        textDay.background = ContextCompat.getDrawable(itemView.context, bgId)
    }
}

class CalendarDiffCallback : BaseDiffCallBack<DateModel>()
