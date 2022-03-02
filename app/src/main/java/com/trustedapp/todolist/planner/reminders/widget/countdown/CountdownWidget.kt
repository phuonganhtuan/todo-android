package com.trustedapp.todolist.planner.reminders.widget.countdown

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.data.datasource.local.database.AppDatabase
import com.trustedapp.todolist.planner.reminders.data.models.entity.CountDownType
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import java.util.*
import java.util.concurrent.TimeUnit


class CountdownWidget : AppWidgetProvider() {

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        val widgetDao = AppDatabase.invoke(context).taskDao()

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.layout_widget_countdown)

            val widgetModel = widgetDao.getCountDownModel(appWidgetId)

            if (widgetModel != null) {
                views.setTextViewText(R.id.textEvent, widgetModel.eventName)
                views.setTextViewText(
                    R.id.textDate,
                    DateTimeUtils.getComparableDateString(
                        Calendar.getInstance().apply { timeInMillis = widgetModel.date }.time
                    )
                )
                views.setImageViewResource(R.id.imageIcon, R.drawable.ic_balloon)
                views.setImageViewResource(R.id.imageDate, R.drawable.ic_date)
                val time = if (widgetModel.countType == CountDownType.REMAIN_DAYS.name) {
                    widgetModel.date - System.currentTimeMillis()
                } else {
                    System.currentTimeMillis() - widgetModel.updateTime
                }
                views.setTextViewText(
                    R.id.textDayCount,
                    TimeUnit.MILLISECONDS.toDays(time).toString()
                )
            }

            val intent = Intent(context, CountDownWidgetSettingActivity::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val pendingIntent =
                PendingIntent.getActivity(context, appWidgetId, intent, FLAG_MUTABLE)
            views.setOnClickPendingIntent(R.id.layoutWidget, pendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }
}

fun updateCountDownWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {

    val widgetDao = AppDatabase.invoke(context).taskDao()

    val views = RemoteViews(context.packageName, R.layout.layout_widget_countdown)

    val widgetModel = widgetDao.getCountDownModel(appWidgetId)

    if (widgetModel != null) {
        views.setTextViewText(R.id.textEvent, widgetModel.eventName)
        views.setTextViewText(
            R.id.textDate,
            DateTimeUtils.getComparableDateString(
                Calendar.getInstance().apply { timeInMillis = widgetModel.date }.time
            )
        )
        views.setImageViewResource(R.id.imageIcon, countDownEventIconIds[widgetModel.iconIndex])
        views.setImageViewResource(R.id.imageDate, R.drawable.ic_date)
        val time = if (widgetModel.countType == CountDownType.REMAIN_DAYS.name) {
            widgetModel.date - System.currentTimeMillis()
        } else {
            System.currentTimeMillis() - widgetModel.updateTime
        }
        views.setTextViewText(
            R.id.textDayCount,
            TimeUnit.MILLISECONDS.toDays(time).toString()
        )
    }

    val intent = Intent(context, CountDownWidgetSettingActivity::class.java).apply {
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    }
    val pendingIntent =
        PendingIntent.getActivity(context, appWidgetId, intent, FLAG_MUTABLE)
    views.setOnClickPendingIntent(R.id.layoutWidget, pendingIntent)
    appWidgetManager.updateAppWidget(appWidgetId, views)
}