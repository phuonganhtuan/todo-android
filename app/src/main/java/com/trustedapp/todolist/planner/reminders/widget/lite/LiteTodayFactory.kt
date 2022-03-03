package com.trustedapp.todolist.planner.reminders.widget.lite

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.data.datasource.local.database.AppDatabase
import com.trustedapp.todolist.planner.reminders.utils.Constants
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import com.trustedapp.todolist.planner.reminders.utils.getStringByLocale
import com.trustedapp.todolist.planner.reminders.widget.standard.StandardWidget
import com.trustedapp.todolist.planner.reminders.widget.standard.WidgetItemWrap
import java.util.*
import kotlin.random.Random

class LiteTodayFactory(private val context: Context, private val intent: Intent?) :
    RemoteViewsService.RemoteViewsFactory {

    private val taskDao by lazy { AppDatabase.invoke(context).taskDao() }
    private var todayTasks = mutableListOf<WidgetItemWrap>()

    private var isOnlyToday = true

    private var categoryId: Int? = null
    private var containCompleted = false

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
                )
            )
                .map { WidgetItemWrap(task = it) }.toMutableList()
        } else {
            taskDao.getTaskInDay(
                DateTimeUtils.getComparableDateString(
                    Calendar.getInstance().time,
                    isDefault = true
                )
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
        val rv = RemoteViews(context.packageName, R.layout.item_widget_lite_task)
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
            rv.setViewVisibility(R.id.layoutWidgetContent, View.VISIBLE)
            rv.setViewVisibility(R.id.textEmpty, View.GONE)
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
        val intentTask = Intent().apply {
            action = StandardWidget.ACTION_CLICK_ITEM
            putExtra(Constants.KEY_TASK_ID, todayTasks[position].task?.task?.id)
        }
        rv.setOnClickFillInIntent(R.id.layoutRoot, intentTask)
        return rv
    }

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(context.packageName, R.layout.item_widget_lite_task)
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int) =
        todayTasks[position].task?.task?.id?.toLong() ?: Random.nextLong()

    override fun hasStableIds() = true
}
