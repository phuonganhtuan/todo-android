package com.trustedapp.todolist.planner.reminders.widget.month

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.screens.newtask.NewTaskActivity
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import java.util.*


class MonthWidget : AppWidgetProvider() {

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val intent = Intent(context, MonthRemoteService::class.java)
            val views = RemoteViews(context.packageName, R.layout.layout_widget_month)
            views.setImageViewResource(R.id.imagePreviousMonth, R.drawable.ic_previous)
            views.setImageViewResource(R.id.imageNextMonth, R.drawable.ic_next)
            views.setTextViewText(
                R.id.textMonthYear,
                DateTimeUtils.getMonthYearString(context, Calendar.getInstance())
            )
            views.setRemoteAdapter(R.id.gridCalendar, intent)
            val intentSetting = Intent(context, NewTaskActivity::class.java)
            val pendingIntentSetting =
                PendingIntent.getActivity(context, 0, intentSetting, FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.imageAddTask, pendingIntentSetting)
            appWidgetManager.updateAppWidget(appWidgetId, views)
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val intent = Intent(context, MonthRemoteService::class.java)
    val views = RemoteViews(context.packageName, R.layout.layout_widget_month)
    views.setImageViewResource(R.id.imagePreviousMonth, R.drawable.ic_previous)
    views.setImageViewResource(R.id.imageNextMonth, R.drawable.ic_next)
    views.setTextViewText(
        R.id.textMonthYear,
        DateTimeUtils.getMonthYearString(context, Calendar.getInstance())
    )
    views.setRemoteAdapter(R.id.gridCalendar, intent)
    val intentSetting = Intent(context, NewTaskActivity::class.java)
    val pendingIntentSetting =
        PendingIntent.getActivity(context, 0, intentSetting, FLAG_IMMUTABLE)
    views.setOnClickPendingIntent(R.id.imageAddTask, pendingIntentSetting)
    appWidgetManager.updateAppWidget(appWidgetId, views)
}
