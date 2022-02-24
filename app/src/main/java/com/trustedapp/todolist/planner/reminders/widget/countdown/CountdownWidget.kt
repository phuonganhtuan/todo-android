package com.trustedapp.todolist.planner.reminders.widget.countdown

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.screens.home.HomeActivity


class CountdownWidget : AppWidgetProvider() {

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.layout_widget_countdown)
            views.setImageViewResource(R.id.imageIcon, R.drawable.ic_balloon)
            views.setImageViewResource(R.id.imageDate, R.drawable.ic_date)
            val intent = Intent(context, HomeActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(context, 0, intent, FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.imageAddTask, pendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }

    internal fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.layout_widget_countdown)
        views.setImageViewResource(R.id.imageIcon, R.drawable.ic_balloon)
        views.setImageViewResource(R.id.imageDate, R.drawable.ic_date)
        val intent = Intent(context, HomeActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.imageAddTask, pendingIntent)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
