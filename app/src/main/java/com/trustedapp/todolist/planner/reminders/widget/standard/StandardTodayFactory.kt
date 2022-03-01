package com.trustedapp.todolist.planner.reminders.widget.standard

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.content.ContextCompat
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.data.datasource.local.database.AppDatabase
import com.trustedapp.todolist.planner.reminders.data.models.entity.TaskShort
import com.trustedapp.todolist.planner.reminders.utils.Constants
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import com.trustedapp.todolist.planner.reminders.widget.standard.StandardWidget.Companion.ACTION_CLICK_ITEM
import com.trustedapp.todolist.planner.reminders.widget.standard.StandardWidget.Companion.ACTION_DONE_ITEM
import java.util.*
import kotlin.random.Random

class StandardTodayFactory(private val context: Context, intent: Intent?) :
    RemoteViewsService.RemoteViewsFactory {

    private val taskDao by lazy { AppDatabase.invoke(context).taskDao() }
    private var todayTasks = mutableListOf<WidgetItemWrap>()

    override fun onCreate() {
    }

    override fun onDataSetChanged() {
        todayTasks =
            taskDao.getTaskInDay(DateTimeUtils.getComparableDateString(Calendar.getInstance().time, isDefault = true))
                .map { WidgetItemWrap(task = it) }.toMutableList()
        val future =
            taskDao.getFutureTask(DateTimeUtils.getTomorrow())
                .map { WidgetItemWrap(task = it, isOther = true) }
                .toMutableList()
        if (future.isEmpty()) {
            future.add(WidgetItemWrap(task = null, isHeader = true, header = "Other"))
        } else {
            future[0].apply {
                isHeader = true
                header = "Other"
            }
        }
        todayTasks.addAll(future)
    }

    override fun onDestroy() {
    }

    override fun getCount() = todayTasks.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.item_widget_standard_task)
        if (todayTasks[position].isHeader) {
            rv.setViewVisibility(R.id.textHeader, View.VISIBLE)
            rv.setTextViewText(R.id.textHeader, todayTasks[position].header)
        } else {
            rv.setViewVisibility(R.id.textHeader, View.GONE)
        }
        if (todayTasks[position].task == null) {
            rv.setViewVisibility(R.id.layoutWidgetContent, View.GONE)
            return rv
        } else {
            rv.setViewVisibility(R.id.layoutWidgetContent, View.VISIBLE)
        }
        rv.setTextViewText(R.id.textTitleWidget, todayTasks[position].task?.task?.title)
        val timeString = if (!todayTasks[position].isOther) {
            DateTimeUtils.getHourMinuteFromMillisecond(
                todayTasks[position].task?.task?.calendar ?: 0L
            )
        } else {
            DateTimeUtils.getShortTimeFromMillisecond(
                todayTasks[position].task?.task?.calendar ?: 0L
            )
        }
        rv.setTextViewText(R.id.textDes, timeString)
        val imageId =
            if (todayTasks[position].task?.task?.isDone == true) {
                R.drawable.ic_checkbox_check_grey
            } else {
                R.drawable.ic_checkbox_uncheck_grey
            }
        rv.setImageViewResource(R.id.imageStatus, imageId)
        val intentTask = Intent().apply {
            action = ACTION_CLICK_ITEM
            putExtra(Constants.KEY_TASK_ID, todayTasks[position].task?.task?.id)
        }

        val intentTaskStatus = Intent().apply {
            action = ACTION_DONE_ITEM
            putExtra(Constants.KEY_TASK_ID, todayTasks[position].task?.task?.id)
        }

        val textColor = if (todayTasks[position].isOther) {
            R.color.color_text_secondary_dark
        } else {
            R.color.color_menu_text_default
        }
        rv.setTextColor(R.id.textTitleWidget, ContextCompat.getColor(context, textColor))

        rv.setOnClickFillInIntent(R.id.imageStatus, intentTaskStatus)
        rv.setOnClickFillInIntent(R.id.layoutRoot, intentTask)
        return rv
    }

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(context.packageName, R.layout.item_widget_standard_task)
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int) =
        todayTasks[position].task?.task?.id?.toLong() ?: Random.nextLong()

    override fun hasStableIds() = true
}

data class WidgetItemWrap(
    var task: TaskShort?,
    var isHeader: Boolean = false,
    var header: String = "",
    var isOther: Boolean = false,
)
