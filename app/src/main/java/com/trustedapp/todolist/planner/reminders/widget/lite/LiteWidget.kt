package com.trustedapp.todolist.planner.reminders.widget.lite

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.screens.home.HomeActivity
import com.trustedapp.todolist.planner.reminders.screens.newtask.NewTaskActivity
import com.trustedapp.todolist.planner.reminders.screens.taskdetail.TaskDetailActivity
import com.trustedapp.todolist.planner.reminders.utils.Constants
import com.trustedapp.todolist.planner.reminders.widget.standard.StandardWidget


class LiteWidget : AppWidgetProvider() {

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val taskId = intent?.getIntExtra(
            Constants.KEY_TASK_ID,
            -1
        ) ?: -1
        if (intent?.action.equals(StandardWidget.ACTION_CLICK_ITEM) && taskId != -1) {
            context?.let {
                it.startActivity(Intent(it, TaskDetailActivity::class.java).apply {
                    putExtra(Constants.KEY_TASK_ID, taskId)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
            return
        }
        val appWidgetManager = AppWidgetManager.getInstance(context)

        context?.let {
            val widget = ComponentName(context, LiteWidget::class.java)
            onUpdate(it, appWidgetManager, appWidgetManager.getAppWidgetIds(widget))
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(widget), R.id.listTasks)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val intent = Intent(context, LiteRemoteService::class.java)
            val views = RemoteViews(context.packageName, R.layout.layout_widget_lite)
            views.setImageViewResource(R.id.imageSetting, R.drawable.ic_setting_white)
            views.setImageViewResource(R.id.imageAddTask, R.drawable.ic_add_task_widget_white)
            views.setRemoteAdapter(R.id.listTasks, intent)
            views.setEmptyView(R.id.listTasks, R.id.textEmptyWidget)
            val intentAddTask = Intent(context, NewTaskActivity::class.java)
            val pendingIntentAddTask =
                PendingIntent.getActivity(context, 0, intentAddTask, FLAG_IMMUTABLE)
            val intentTaskDetail = Intent(context, LiteWidget::class.java)
            val pendingIntentTaskDetail =
                PendingIntent.getBroadcast(
                    context,
                    1,
                    intentTaskDetail,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )

            val intentHome = Intent(context, HomeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            val pendingIntentHome =
                PendingIntent.getActivity(
                    context,
                    0,
                    intentHome,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )

            views.setOnClickPendingIntent(R.id.layoutWidget, pendingIntentHome)
            views.setOnClickPendingIntent(R.id.imageAddTask, pendingIntentAddTask)
            views.setPendingIntentTemplate(R.id.listTasks, pendingIntentTaskDetail)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }
}