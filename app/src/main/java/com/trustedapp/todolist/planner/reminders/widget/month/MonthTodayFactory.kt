package com.trustedapp.todolist.planner.reminders.widget.month

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.content.ContextCompat
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.data.datasource.local.database.AppDatabase
import com.trustedapp.todolist.planner.reminders.data.models.model.DateModel
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import java.util.*

class MonthTodayFactory(private val context: Context, private val intent: Intent?) :
    RemoteViewsService.RemoteViewsFactory {

    private val taskDao by lazy { AppDatabase.invoke(context).taskDao() }
    private var days = listOf<DateModel>()

    override fun onCreate() {
    }

    override fun onDataSetChanged() {
        val month = intent?.extras?.getLong(MonthWidget.INTENT_MONTH, Calendar.getInstance().timeInMillis) ?: Calendar.getInstance().timeInMillis
        days = getDays(MonthWidget.currentMonth)
    }

    override fun onDestroy() {
    }

    override fun getCount() = days.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.item_calendar_widget)
        val item = days[position]
        rv.setTextViewText(
            R.id.textDay,
            Calendar.getInstance().apply { time = item.date }.get(Calendar.DAY_OF_MONTH)
                .toString()
        )

        val isSelected =
            DateTimeUtils.getComparableDateString(item.date) == DateTimeUtils.getComparableDateString(
                Calendar.getInstance().time
            )
        val textColor = if (isSelected) {
            R.color.white
        } else {
            if (item.isInMonth) R.color.color_cal_primary else R.color.color_cal_secondary
        }
        rv.setTextColor(R.id.textDay, ContextCompat.getColor(context, textColor))

        rv.setViewVisibility(R.id.viewSelect, if (isSelected) View.VISIBLE else View.GONE)
        rv.setViewVisibility(R.id.viewHasTask, if (item.hasTask) View.VISIBLE else View.GONE)
        rv.setImageViewResource(R.id.imageBalloon, R.drawable.ic_balloon)
        rv.setViewVisibility(
            R.id.imageBalloon, if (item.hasBirthday) View.VISIBLE else View.GONE
        )
        return rv
    }

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(context.packageName, R.layout.item_widget_standard_task)
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int) =
        days[position].id.toLong()

    override fun hasStableIds() = true

    private fun getDays(month: Calendar) = DateTimeUtils.getDaysOfMonth(month).map {
        val isInMonth =
            Calendar.getInstance().apply { time = it }
                .get(Calendar.MONTH) == month.get(
                Calendar.MONTH
            )
        val tasks = taskDao.getTaskInDay(DateTimeUtils.getComparableDateString(it, isDefault = true))
        val tasksCatName = tasks.map { t -> t.category?.name.toString().lowercase() }
        DateModel(
            id = it.time.toInt(),
            date = it,
            isInMonth = isInMonth,
            hasTask = tasks.isNotEmpty(),
            hasBirthday = tasksCatName.contains("birthday")
        )
    }
}
