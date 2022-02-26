package com.trustedapp.todolist.planner.reminders.widget.standard

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.data.datasource.local.database.AppDatabase
import com.trustedapp.todolist.planner.reminders.screens.home.HomeActivity
import com.trustedapp.todolist.planner.reminders.screens.newtask.NewTaskActivity
import com.trustedapp.todolist.planner.reminders.screens.taskdetail.TaskDetailActivity
import com.trustedapp.todolist.planner.reminders.utils.Constants
import com.trustedapp.todolist.planner.reminders.widget.month.MonthWidget


class StandardWidget : AppWidgetProvider() {

    companion object {
        const val ACTION_CLICK_ITEM = "Click_item"
        const val ACTION_DONE_ITEM = "Done_item"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val taskId = intent?.getIntExtra(
            Constants.KEY_TASK_ID,
            -1
        ) ?: -1
        if (intent?.action.equals(ACTION_DONE_ITEM) && taskId != -1) {
            context?.let {
                val dao = AppDatabase.invoke(context).taskDao()
                val task = dao.getTask(taskId)
                task.task.isDone = true
                dao.updateTaskNoSuspend(task.task)

                val appWidgetManager = AppWidgetManager.getInstance(context)

                val widget = ComponentName(context, StandardWidget::class.java)

                appWidgetManager.notifyAppWidgetViewDataChanged(
                    appWidgetManager.getAppWidgetIds(
                        widget
                    ), R.id.listTasks
                )
            }
            return
        }
        if (intent?.action.equals(ACTION_CLICK_ITEM) && taskId != -1) {
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
            val widget = ComponentName(context, StandardWidget::class.java)
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
            val intent = Intent(context, StandardRemoteService::class.java)
            val views = RemoteViews(context.packageName, R.layout.layout_widget_standard)
            views.setImageViewResource(R.id.imageSetting, R.drawable.ic_setting)
            views.setImageViewResource(R.id.imageAddTask, R.drawable.ic_add_task_widget)
            views.setRemoteAdapter(R.id.listTasks, intent)
            views.setEmptyView(R.id.listTasks, R.id.textEmptyWidget)
            val intentTask = Intent(context, NewTaskActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    0,
                    intentTask,
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

            val intentTaskStatus = Intent(context, StandardWidget::class.java)
            val pendingIntentTaskStatus =
                PendingIntent.getBroadcast(
                    context,
                    1,
                    intentTaskStatus,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )

            views.setOnClickPendingIntent(R.id.layoutWidget, pendingIntentHome)
            views.setOnClickPendingIntent(R.id.imageAddTask, pendingIntent)
            views.setPendingIntentTemplate(R.id.listTasks, pendingIntentTaskStatus)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }
}
