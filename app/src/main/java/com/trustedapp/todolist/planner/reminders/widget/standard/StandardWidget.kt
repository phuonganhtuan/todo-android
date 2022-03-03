package com.trustedapp.todolist.planner.reminders.widget.standard

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.data.datasource.local.database.AppDatabase
import com.trustedapp.todolist.planner.reminders.screens.home.HomeActivity
import com.trustedapp.todolist.planner.reminders.screens.newtask.NewTaskActivity
import com.trustedapp.todolist.planner.reminders.screens.taskdetail.TaskDetailActivity
import com.trustedapp.todolist.planner.reminders.utils.Constants
import com.trustedapp.todolist.planner.reminders.utils.getStringByLocale
import com.trustedapp.todolist.planner.reminders.utils.helper.getCatName
import com.trustedapp.todolist.planner.reminders.widget.widgetBgColors
import com.trustedapp.todolist.planner.reminders.widget.widgetBgsRoundTop


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
                task.task.isDone = !task.task.isDone
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
            appWidgetManager.notifyAppWidgetViewDataChanged(
                appWidgetManager.getAppWidgetIds(widget),
                R.id.listTasks
            )
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        val dao = AppDatabase.invoke(context).taskDao()

        for (appWidgetId in appWidgetIds) {
            val intent = Intent(context, StandardRemoteService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val views = RemoteViews(context.packageName, R.layout.layout_widget_standard)

            val widgetModel = dao.getStandardWidgetModel(appWidgetId)

            views.setImageViewResource(R.id.imageSetting, R.drawable.ic_setting)
            views.setImageViewResource(R.id.imageAddTask, R.drawable.ic_add_task_widget)
            views.setRemoteAdapter(R.id.listTasks, intent)
            views.setEmptyView(R.id.listTasks, R.id.textEmptyWidget)
            val intentTask = Intent(context, NewTaskActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    appWidgetId,
                    intentTask,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )

            val intentHome = Intent(context, HomeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            val pendingIntentHome =
                PendingIntent.getActivity(
                    context,
                    appWidgetId,
                    intentHome,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )

            val intentTaskStatus = Intent(context, StandardWidget::class.java)
            val pendingIntentTaskStatus =
                PendingIntent.getBroadcast(
                    context,
                    appWidgetId,
                    intentTaskStatus,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
            val intentSetting = Intent(context, StandardWidgetSettingActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val pendingIntentSetting =
                PendingIntent.getActivity(
                    context,
                    appWidgetId,
                    intentSetting,
                    PendingIntent.FLAG_IMMUTABLE
                )

            views.setOnClickPendingIntent(R.id.imageSetting, pendingIntentSetting)
            views.setOnClickPendingIntent(R.id.layoutWidget, pendingIntentHome)
            views.setOnClickPendingIntent(R.id.imageAddTask, pendingIntent)
            views.setPendingIntentTemplate(R.id.listTasks, pendingIntentTaskStatus)

            if (widgetModel != null) {
                val bg = widgetBgsRoundTop[widgetBgColors.indexOf(widgetModel.color)]
                val textTitleColor = ContextCompat.getColor(
                    context,
                    if (widgetModel.isDark) R.color.white else R.color.color_menu_text_default
                )
                views.setFloat(R.id.viewBgTitle, "setAlpha", widgetModel.alpha / 100f)
                views.setFloat(R.id.viewBg, "setAlpha", widgetModel.alpha / 100f)
                views.setTextColor(R.id.textTitleWidget, textTitleColor)

                views.setInt(R.id.viewBgTitle, "setBackgroundResource", bg)

                val titlePrefix =
                    if (widgetModel.isOnlyToday) context.getStringByLocale(R.string.today) else context.getStringByLocale(
                        R.string.tasks
                    )
                var titleCat = ""
                if (widgetModel.categoryId != null) {
                    val cat = dao.getCategory(widgetModel.categoryId!!)
                    if (cat != null) {
                        titleCat = " (${getCatName(context, cat.name)})"
                    }
                }
                views.setTextViewText(R.id.textTitleWidget, titlePrefix + titleCat)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }
}

fun updateStandardWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {

    val dao = AppDatabase.invoke(context).taskDao()

    val intent = Intent(context, StandardRemoteService::class.java).apply {
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    }
    val views = RemoteViews(context.packageName, R.layout.layout_widget_standard)

    val widgetModel = dao.getStandardWidgetModel(appWidgetId)

    views.setImageViewResource(R.id.imageSetting, R.drawable.ic_setting)
    views.setImageViewResource(R.id.imageAddTask, R.drawable.ic_add_task_widget)
    views.setRemoteAdapter(R.id.listTasks, intent)
    views.setEmptyView(R.id.listTasks, R.id.textEmptyWidget)
    val intentTask = Intent(context, NewTaskActivity::class.java)
    val pendingIntent =
        PendingIntent.getActivity(
            context,
            appWidgetId,
            intentTask,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

    val intentHome = Intent(context, HomeActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }
    val pendingIntentHome =
        PendingIntent.getActivity(
            context,
            appWidgetId,
            intentHome,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

    val intentTaskStatus = Intent(context, StandardWidget::class.java)
    val pendingIntentTaskStatus =
        PendingIntent.getBroadcast(
            context,
            appWidgetId,
            intentTaskStatus,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

    val intentSetting = Intent(context, StandardWidgetSettingActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    }
    val pendingIntentSetting =
        PendingIntent.getActivity(context, appWidgetId, intentSetting, PendingIntent.FLAG_IMMUTABLE)

    views.setOnClickPendingIntent(R.id.imageSetting, pendingIntentSetting)
    views.setOnClickPendingIntent(R.id.layoutWidget, pendingIntentHome)
    views.setOnClickPendingIntent(R.id.imageAddTask, pendingIntent)
    views.setPendingIntentTemplate(R.id.listTasks, pendingIntentTaskStatus)

    if (widgetModel != null) {
        val bg = widgetBgsRoundTop[widgetBgColors.indexOf(widgetModel.color)]
        val textTitleColor = ContextCompat.getColor(
            context,
            if (widgetModel.isDark) R.color.white else R.color.color_menu_text_default
        )
        views.setFloat(R.id.viewBgTitle, "setAlpha", widgetModel.alpha / 100f)
        views.setFloat(R.id.viewBg, "setAlpha", widgetModel.alpha / 100f)
        views.setTextColor(R.id.textTitleWidget, textTitleColor)

        views.setInt(R.id.viewBgTitle, "setBackgroundResource", bg)

        val titlePrefix =
            if (widgetModel.isOnlyToday) context.getStringByLocale(R.string.today) else context.getStringByLocale(
                R.string.tasks
            )
        var titleCat = ""
        if (widgetModel.categoryId != null) {
            val cat = dao.getCategory(widgetModel.categoryId!!)
            if (cat != null) {
                titleCat = " (${getCatName(context, cat.name)})"
            }
        }
        views.setTextViewText(R.id.textTitleWidget, titlePrefix + titleCat)
    }

    appWidgetManager.updateAppWidget(appWidgetId, views)
}