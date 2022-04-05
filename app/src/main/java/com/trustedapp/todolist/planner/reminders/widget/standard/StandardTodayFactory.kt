package com.trustedapp.todolist.planner.reminders.widget.standard

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.content.ContextCompat
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.data.datasource.local.database.AppDatabase
import com.trustedapp.todolist.planner.reminders.data.models.entity.BaseEntity
import com.trustedapp.todolist.planner.reminders.data.models.entity.TaskShort
import com.trustedapp.todolist.planner.reminders.utils.Constants
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import com.trustedapp.todolist.planner.reminders.utils.getStringByLocale
import com.trustedapp.todolist.planner.reminders.widget.standard.StandardWidget.Companion.ACTION_CLICK_ITEM
import com.trustedapp.todolist.planner.reminders.widget.standard.StandardWidget.Companion.ACTION_DONE_ITEM
import java.util.*
import kotlin.random.Random

class StandardTodayFactory(private val context: Context, private val intent: Intent?) :
    RemoteViewsService.RemoteViewsFactory {

    private val taskDao by lazy { AppDatabase.invoke(context).taskDao() }
    private var todayTasks = mutableListOf<WidgetItemWrap>()

    private var isOnlyToday = false

    private var categoryId: Int? = null
    private var containCompleted = true

    override fun onCreate() {
    }

    override fun onDataSetChanged() {

        val id = intent?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID)

        id?.let {
            val widgetModel = taskDao.getStandardWidgetModel(id)
            if (widgetModel != null) {
                isOnlyToday = widgetModel.isOnlyToday
                categoryId = widgetModel.categoryId
                containCompleted = widgetModel.containCompleted
            }
        }

        todayTasks = if (containCompleted) {
            taskDao.getTaskInDayAll(
                DateTimeUtils.getComparableDateString(
                    Calendar.getInstance().time,
                    isDefault = true
                ),
                DateTimeUtils.getStartOfDay(Calendar.getInstance().time).time,
                DateTimeUtils.getStartOfNextDay(Calendar.getInstance().time).time
            )
                .map { WidgetItemWrap(task = it) }.toMutableList()
        } else {
            taskDao.getTaskInDay(
                DateTimeUtils.getComparableDateString(
                    Calendar.getInstance().time,
                    isDefault = true
                ),
                DateTimeUtils.getStartOfDay(Calendar.getInstance().time).time,
                DateTimeUtils.getStartOfNextDay(Calendar.getInstance().time).time
            )
                .map { WidgetItemWrap(task = it) }.toMutableList()
        }
        if (categoryId != null) {
            todayTasks =
                todayTasks.filter { item -> item.task == null || item.task?.task?.categoryId == categoryId }
                    .toMutableList()
        }
        if (todayTasks.isEmpty()) {
            todayTasks.add(
                WidgetItemWrap(
                    task = null,
                    isHeader = !isOnlyToday,
                    header = context.getStringByLocale(R.string.today)
                )
            )
        } else {
            todayTasks[0].apply {
                isHeader = !isOnlyToday
                header = context.getStringByLocale(R.string.today)
            }
        }
        if (!isOnlyToday) {
            var future = if (containCompleted) {
                taskDao.getFutureTaskAll(DateTimeUtils.getTomorrow())
                    .map { WidgetItemWrap(task = it, isOther = true) }
                    .toMutableList()
            } else {
                taskDao.getFutureTask(DateTimeUtils.getTomorrow())
                    .map { WidgetItemWrap(task = it, isOther = true) }
                    .toMutableList()
            }
            if (categoryId != null) {
                future =
                    future.filter { item -> item.task == null || item.task?.task?.categoryId == categoryId }
                        .toMutableList()
            }
            if (future.isEmpty()) {
                future.add(
                    WidgetItemWrap(
                        task = null,
                        isHeader = !isOnlyToday,
                        header = context.getStringByLocale(R.string.future_task)
                    )
                )
            } else {
                future[0].apply {
                    isHeader = !isOnlyToday
                    header = context.getStringByLocale(R.string.future_task)
                }
            }
            todayTasks.addAll(future)
        }
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
            rv.setViewVisibility(R.id.textEmpty, View.VISIBLE)
            return rv
        } else {
            rv.setViewVisibility(R.id.textEmpty, View.GONE)
            rv.setViewVisibility(R.id.layoutWidgetContent, View.VISIBLE)
        }
        rv.setTextViewText(R.id.textTitleWidget, todayTasks[position].task?.task?.title)
        val timeString = if (!todayTasks[position].isOther) {
            if (todayTasks[position].task?.task?.dueDate.isNullOrEmpty()) {
                ""
            } else {
                DateTimeUtils.getHourMinuteFromMillisecond(
                    todayTasks[position].task?.task?.calendar ?: 0L
                )
            }
        } else {
            if (todayTasks[position].task?.task?.dueDate.isNullOrEmpty()) {
                DateTimeUtils.getDayMonthFromMillisecond(
                    todayTasks[position].task?.task?.calendar ?: 0L
                )
            } else {
                DateTimeUtils.getShortTimeFromMillisecond(
                    todayTasks[position].task?.task?.calendar ?: 0L
                )
            }
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
    override var id: Int = 0,
    var task: TaskShort?,
    var isHeader: Boolean = false,
    var header: String = "",
    var isOther: Boolean = false,
) : BaseEntity()
