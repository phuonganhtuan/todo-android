package com.trustedapp.todolist.planner.reminders.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.screens.newtask.NewTaskActivity
import com.trustedapp.todolist.planner.reminders.utils.SPUtils

object NotificationHelper {

    fun cancelAddTaskNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(-1)
    }

    fun createAddTaskNotification(context: Context) {

        val id = -1
        val intent1 = Intent(context, NewTaskActivity::class.java)
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent =
            PendingIntent.getActivity(context, id, intent1, PendingIntent.FLAG_MUTABLE)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, "channel_id_add")
        builder.setSmallIcon(R.drawable.ic_time_outline)
        builder.setContentText(context.getString(R.string.app_name))
        builder.setContentTitle(context.getString(R.string.create_task_suggest))
        builder.setDefaults(NotificationCompat.DEFAULT_VIBRATE)
        builder.setAutoCancel(false)
        builder.priority = Notification.PRIORITY_DEFAULT
        builder.setOnlyAlertOnce(true)
        builder.setSound(null)
        builder.setVisibility(if (SPUtils.getIsScreenlockTaskReminder(context)) NotificationCompat.VISIBILITY_PUBLIC else NotificationCompat.VISIBILITY_SECRET)
        builder.setCategory(NotificationCompat.CATEGORY_ALARM)
        builder.build().flags = Notification.PRIORITY_DEFAULT
        builder.setContentIntent(pendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "channel_id_add"
            val channel =
                NotificationChannel(
                    channelId,
                    "channel name notification",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            channel.enableVibration(false)
            channel.setSound(null, null)
            channel.lockscreenVisibility =
                if (SPUtils.getIsScreenlockTaskReminder(context)) NotificationCompat.VISIBILITY_PUBLIC else NotificationCompat.VISIBILITY_SECRET
            notificationManager.createNotificationChannel(channel)
            builder.setChannelId(channelId)
        }
        val notification: Notification = builder.build()
        notificationManager.cancel(id)
        notificationManager.notify(id, notification)
    }
}
