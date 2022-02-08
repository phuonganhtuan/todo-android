package com.example.todo.alarm

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todo.data.datasource.local.database.AppDatabase

internal class SchedularBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> {
                val tasks = AppDatabase.invoke(context).taskDao()
                    .getTasksForAlarm(System.currentTimeMillis())
                tasks.filter { it.task.isDone && it.detail.isReminder }.forEach { task ->
                    if (task.reminder != null) {
                        ScheduleHelper.addAlarm(context, task.task, task.reminder!!)
                    }
                }
            }
        }
    }
}

internal class SchedularBootedBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> {
                val tasks = AppDatabase.invoke(context).taskDao()
                    .getTasksForAlarm(System.currentTimeMillis())
                tasks.filter { it.task.isDone && it.detail.isReminder }.forEach { task ->
                    if (task.reminder != null) {
                        ScheduleHelper.addAlarm(context, task.task, task.reminder!!)
                    }
                }
            }
        }
    }
}
