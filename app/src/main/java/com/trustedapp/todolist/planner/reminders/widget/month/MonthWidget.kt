package com.trustedapp.todolist.planner.reminders.widget.month

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.screens.home.HomeActivity
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import java.util.*
import java.util.Calendar.*


class MonthWidget : AppWidgetProvider() {

    companion object {
        const val PREVIOUS_MONTH_ACTION = "pma"
        const val NEXT_MONTH_ACTION = "nma"
        const val INTENT_MONTH = "current_month"
        var currentMonth = Calendar.getInstance()

        fun getPendingSelfIntent(context: Context?, action: String?): PendingIntent? {
            val intent = Intent(context, MonthWidget::class.java)
            intent.action = action
            return PendingIntent.getBroadcast(
                context,
                0,
                intent,
                FLAG_IMMUTABLE or FLAG_CANCEL_CURRENT
            )
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        when (intent?.action) {
            PREVIOUS_MONTH_ACTION -> toPreviousMonth()
            NEXT_MONTH_ACTION -> toNextMonth()
            Intent.ACTION_DATE_CHANGED -> currentMonth = getInstance()
        }
        val appWidgetManager = AppWidgetManager.getInstance(context)

        context?.let {
            val widget = ComponentName(context, MonthWidget::class.java)
            onUpdate(it, appWidgetManager, appWidgetManager.getAppWidgetIds(widget))
            appWidgetManager.notifyAppWidgetViewDataChanged(
                appWidgetManager.getAppWidgetIds(widget),
                R.id.gridCalendar
            )
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val intent = Intent(context, MonthRemoteService::class.java).apply {
                putExtra(INTENT_MONTH, currentMonth.timeInMillis)
            }
            val views = RemoteViews(context.packageName, R.layout.layout_widget_month)
            views.setImageViewResource(R.id.imagePreviousMonth, R.drawable.ic_previous)
            views.setImageViewResource(R.id.imageNextMonth, R.drawable.ic_next)
            views.setTextViewText(
                R.id.textMonthYear,
                DateTimeUtils.getMonthYearString(currentMonth)
            )

            val dayTitle = DateTimeUtils.getCalendarDayTitle()
            views.setTextViewText(R.id.textDay1, dayTitle[0])
            views.setTextViewText(R.id.textDay2, dayTitle[1])
            views.setTextViewText(R.id.textDay3, dayTitle[2])
            views.setTextViewText(R.id.textDay4, dayTitle[3])
            views.setTextViewText(R.id.textDay5, dayTitle[4])
            views.setTextViewText(R.id.textDay6, dayTitle[5])
            views.setTextViewText(R.id.textDay7, dayTitle[6])

            views.setRemoteAdapter(R.id.gridCalendar, intent)
            views.setOnClickPendingIntent(
                R.id.imagePreviousMonth,
                getPendingSelfIntent(context, PREVIOUS_MONTH_ACTION)
            )
            views.setOnClickPendingIntent(
                R.id.imageNextMonth,
                getPendingSelfIntent(context, NEXT_MONTH_ACTION)
            )

            val intentHome = Intent(context, HomeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            val pendingIntent =
                PendingIntent.getActivity(context, 0, intentHome, FLAG_IMMUTABLE)

            views.setOnClickPendingIntent(R.id.layoutWidget, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }

    private fun toNextMonth() {
        currentMonth.set(DAY_OF_MONTH, 1)
        currentMonth.set(MONTH, currentMonth.get(MONTH) + 1)
    }

    private fun toPreviousMonth() {
        currentMonth.set(DAY_OF_MONTH, 1)
        currentMonth.set(MONTH, currentMonth.get(MONTH) - 1)
    }
}

fun updateMonthWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray
) {
    for (appWidgetId in appWidgetIds) {
        val intent = Intent(context, MonthRemoteService::class.java).apply {
            putExtra(MonthWidget.INTENT_MONTH, MonthWidget.currentMonth.timeInMillis)
        }
        val views = RemoteViews(context.packageName, R.layout.layout_widget_month)
        views.setImageViewResource(R.id.imagePreviousMonth, R.drawable.ic_previous)
        views.setImageViewResource(R.id.imageNextMonth, R.drawable.ic_next)
        views.setTextViewText(
            R.id.textMonthYear,
            DateTimeUtils.getMonthYearString(MonthWidget.currentMonth)
        )

        val dayTitle = DateTimeUtils.getCalendarDayTitle()
        views.setTextViewText(R.id.textDay1, dayTitle[0])
        views.setTextViewText(R.id.textDay2, dayTitle[1])
        views.setTextViewText(R.id.textDay3, dayTitle[2])
        views.setTextViewText(R.id.textDay4, dayTitle[3])
        views.setTextViewText(R.id.textDay5, dayTitle[4])
        views.setTextViewText(R.id.textDay6, dayTitle[5])
        views.setTextViewText(R.id.textDay7, dayTitle[6])

        views.setRemoteAdapter(R.id.gridCalendar, intent)
        views.setOnClickPendingIntent(
            R.id.imagePreviousMonth,
            MonthWidget.getPendingSelfIntent(context, MonthWidget.PREVIOUS_MONTH_ACTION)
        )
        views.setOnClickPendingIntent(
            R.id.imageNextMonth,
            MonthWidget.getPendingSelfIntent(context, MonthWidget.NEXT_MONTH_ACTION)
        )

        val intentHome = Intent(context, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intentHome, FLAG_IMMUTABLE)

        views.setOnClickPendingIntent(R.id.layoutWidget, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
