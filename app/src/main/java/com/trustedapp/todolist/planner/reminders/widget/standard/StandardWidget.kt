package com.trustedapp.todolist.planner.reminders.widget.standard

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.screens.newtask.NewTaskActivity
import com.trustedapp.todolist.planner.reminders.screens.taskdetail.TaskDetailActivity
import com.trustedapp.todolist.planner.reminders.utils.Constants


class StandardWidget : AppWidgetProvider() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val taskId = intent?.getIntExtra(
            Constants.KEY_TASK_ID,
            -1
        )
        if (taskId != null && taskId != -1) {
            context?.let {
                it.startActivity(Intent(it, TaskDetailActivity::class.java).apply {
                    putExtra(
                        Constants.KEY_TASK_ID,
                        taskId
                    )
                })
            }
        }
        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val intent = Intent(context, StandardRemoteService::class.java)
            val views = RemoteViews(context.packageName, R.layout.layout_widget_standard)
            views.setImageViewResource(R.id.imageSetting, R.drawable.ic_setting)
            views.setImageViewResource(R.id.imageAddTask, R.drawable.ic_add_task_widget)
            views.setRemoteAdapter(R.id.listTasks, intent)
            views.setEmptyView(R.id.listTasks, R.id.textEmptyWidget)
            val intentAddTask = Intent(context, NewTaskActivity::class.java)
            val pendingIntentAddTask =
                PendingIntent.getActivity(context, 0, intentAddTask, FLAG_IMMUTABLE)

            val intentTask = Intent(context, TaskDetailActivity::class.java).apply {
                    putExtra(Constants.KEY_TASK_ID, -1)
                }
            val pendingIntentTask =
                PendingIntent.getActivity(context, 0, intentTask, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
            views.setOnClickPendingIntent(R.id.imageAddTask, pendingIntentAddTask)
            views.setPendingIntentTemplate(R.id.listTasks, pendingIntentTask)
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
    val intent = Intent(context, StandardRemoteService::class.java)
    val views = RemoteViews(context.packageName, R.layout.layout_widget_standard)
    views.setImageViewResource(R.id.imageSetting, R.drawable.ic_setting)
    views.setImageViewResource(R.id.imageAddTask, R.drawable.ic_add_task_widget)
    views.setRemoteAdapter(R.id.listTasks, intent)
    views.setEmptyView(R.id.listTasks, R.id.textEmptyWidget)
    val intentAddTask = Intent(context, NewTaskActivity::class.java)
    val pendingIntentAddTask = PendingIntent.getActivity(context, 0, intentAddTask, FLAG_IMMUTABLE)
    val intentTask = Intent(context, TaskDetailActivity::class.java).apply {
        putExtra(Constants.KEY_TASK_ID, -1)
    }
    val pendingIntentTask =
        PendingIntent.getActivity(context, 0, intentTask, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
    views.setOnClickPendingIntent(R.id.imageAddTask, pendingIntentAddTask)
    views.setPendingIntentTemplate(R.id.listTasks, pendingIntentTask)
    appWidgetManager.updateAppWidget(appWidgetId, views)
}