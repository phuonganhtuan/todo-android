package com.trustedapp.todolist.planner.reminders.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.trustedapp.todolist.planner.reminders.data.models.entity.ReminderEntity
import com.trustedapp.todolist.planner.reminders.data.models.entity.TaskEntity
import com.trustedapp.todolist.planner.reminders.screens.newtask.CustomReminderTimeUnitEnum
import com.trustedapp.todolist.planner.reminders.screens.newtask.ReminderTimeEnum
import com.trustedapp.todolist.planner.reminders.screens.newtask.ReminderTypeEnum
import com.trustedapp.todolist.planner.reminders.screens.newtask.RepeatAtEnum
import com.trustedapp.todolist.planner.reminders.utils.Constants
import java.util.*
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR


@SuppressLint("ServiceCast")
object ScheduleHelper {

    private var alarmManager: AlarmManager? = null

    private const val HOUR_IN_MILLIS = 3600000L

    fun addAlarm(
        context: Context,
        task: TaskEntity,
        reminder: ReminderEntity
    ) {
        if ((task.calendar ?: 0L) < System.currentTimeMillis()) return
        if (alarmManager == null) {
            alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager?.canScheduleExactAlarms() != true) return
        }

        val alarmIntent = Intent(context, AlarmHelper::class.java).apply {
            action = "action"
            putExtra(Constants.KEY_TASK_ID, task.id)
            putExtra(Constants.KEY_TASK_TITLE, task.title)
            putExtra(Constants.KEY_TASK_TIME, task.calendar)
            putExtra(Constants.KEY_REMINDER_TYPE, reminder.reminderType)
            putExtra(Constants.KEY_SCREEN_LOCK_ENABLED, reminder.screenLockReminder)
        }
        val pendingIntent =
            PendingIntent.getBroadcast(context, task.id, alarmIntent, FLAG_MUTABLE or PendingIntent.FLAG_CANCEL_CURRENT)

        val reminderTime = when (reminder.reminderTime) {
            ReminderTimeEnum.THIRTY_MINUTES_BEFORE.name -> -1800000L
            ReminderTimeEnum.SAME_DUE_DATE.name -> 0L
            ReminderTimeEnum.FIVE_MINUTES_BEFORE.name -> -300000L
            ReminderTimeEnum.FIFTEEN_MINUTES_BEFORE.name -> -900000L
            ReminderTimeEnum.TEN_MINUTES_BEFORE.name -> -600000L
            ReminderTimeEnum.ONE_DAY_BEFORE.name -> -86400000L
            ReminderTimeEnum.TWO_DAYS_BEFORE.name -> -172800000L
            ReminderTimeEnum.CUSTOM_DAY_BEFORE.name -> -1L * reminder.customReminderTime * when(reminder.customReminderTimeUnit){
                CustomReminderTimeUnitEnum.MINUTE_UNIT.name -> CustomReminderTimeUnitEnum.MINUTE_UNIT.getOffset()
                CustomReminderTimeUnitEnum.HOUR_UNIT.name -> CustomReminderTimeUnitEnum.HOUR_UNIT.getOffset()
                CustomReminderTimeUnitEnum.DAY_UNIT.name -> CustomReminderTimeUnitEnum.DAY_UNIT.getOffset()
                CustomReminderTimeUnitEnum.WEEK_UNIT.name -> CustomReminderTimeUnitEnum.WEEK_UNIT.getOffset()
                else -> CustomReminderTimeUnitEnum.MINUTE_UNIT.getOffset()
            }
            else -> 0L
        }

        if (reminder.enableRepeat) {
            val repeatTime = when (reminder.repeatTime) {
                RepeatAtEnum.HOUR.name -> HOUR_IN_MILLIS
                RepeatAtEnum.DAILY.name -> HOUR_IN_MILLIS * 24L
                RepeatAtEnum.WEEKLY.name -> HOUR_IN_MILLIS * 24L * 7L
                RepeatAtEnum.MONTHLY.name -> Calendar.getInstance().apply {
                    timeInMillis = task.calendar ?: 0L
                    add(MONTH, 1)
                }.timeInMillis - (task.calendar ?: 0L)
                RepeatAtEnum.YEARLY.name -> Calendar.getInstance().apply {
                    timeInMillis = task.calendar ?: 0L
                    add(YEAR, 1)
                }.timeInMillis - (task.calendar ?: 0L)
                else -> 0
            }
            alarmManager?.setRepeating(
                AlarmManager.RTC_WAKEUP,
                (task.calendar ?: 0L) + reminderTime,
                repeatTime,
                pendingIntent
            )
        } else {
            alarmManager?.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, (task.calendar ?: 0L) + reminderTime, pendingIntent
            )
        }
        Log.d("aaa", "set.")
    }

    fun createTestAlarm(context: Context) {
        if (alarmManager == null) {
            alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        }
        val alarmIntent = Intent(context, AlarmHelper::class.java).apply {
            action = "action"
            putExtra(Constants.KEY_TASK_ID, 3)
            putExtra(Constants.KEY_TASK_TITLE, "task.title")
            putExtra(Constants.KEY_TASK_TIME, System.currentTimeMillis())
            putExtra(Constants.KEY_REMINDER_TYPE, ReminderTypeEnum.ALARM)
            putExtra(Constants.KEY_SCREEN_LOCK_ENABLED, true)
        }
        val pendingIntent =
            PendingIntent.getBroadcast(context, 3, alarmIntent, FLAG_MUTABLE or PendingIntent.FLAG_CANCEL_CURRENT)
        alarmManager?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000L, pendingIntent
        )
    }

    fun cancelAlarm(context: Context, task: TaskEntity, reminder: ReminderEntity) {
        if (alarmManager == null) {
            alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        }
        val alarmIntent = Intent(context, AlarmHelper::class.java).apply {
            action = "action"
            putExtra(Constants.KEY_TASK_ID, task.id)
            putExtra(Constants.KEY_TASK_TITLE, task.title)
            putExtra(Constants.KEY_TASK_TIME, task.calendar)
            putExtra(Constants.KEY_REMINDER_TYPE, reminder.reminderType)
            putExtra(Constants.KEY_SCREEN_LOCK_ENABLED, reminder.screenLockReminder)
        }
        val pendingIntent =
            PendingIntent.getBroadcast(context, task.id, alarmIntent, FLAG_MUTABLE or PendingIntent.FLAG_CANCEL_CURRENT)
        try {
            alarmManager?.cancel(pendingIntent)
        } catch (exception: Exception) {

        }
    }
}
