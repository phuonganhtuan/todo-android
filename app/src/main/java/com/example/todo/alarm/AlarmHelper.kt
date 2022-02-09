package com.example.todo.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todo.R
import com.example.todo.screens.newtask.ReminderTypeEnum
import com.example.todo.screens.taskdetail.TaskDetailActivity
import com.example.todo.utils.Constants
import com.example.todo.utils.DateTimeUtils


class AlarmHelper : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.extras?.getString(Constants.KEY_REMINDER_TYPE)) {
            ReminderTypeEnum.NOTIFICATION.name -> createNotification(context, intent)
            ReminderTypeEnum.ALARM.name -> createAlarm(context, intent)
            else -> return
        }
    }

    private fun createNotification(context: Context, intent: Intent) {
        val bundle: Bundle = intent.extras ?: Bundle()

        val id = bundle.getInt(Constants.KEY_TASK_ID)
        val intent1 = Intent(context, TaskDetailActivity::class.java)
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent1.putExtra(Constants.KEY_TASK_ID, id)

        val pendingIntent =
            PendingIntent.getActivity(context, id, intent1, PendingIntent.FLAG_MUTABLE)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(context, "notify_001")
        mBuilder.setSmallIcon(R.drawable.ic_time_outline)
        mBuilder.setContentText(
            DateTimeUtils.getShortTimeFromMillisecond(
                bundle.getLong(
                    Constants.KEY_TASK_TIME,
                    System.currentTimeMillis()
                )
            )
        )
        mBuilder.setContentTitle(
            bundle.getString(
                Constants.KEY_TASK_TITLE,
                context.getString(R.string.incoming_task)
            )
        )
        mBuilder.setAutoCancel(true)
        mBuilder.priority = Notification.PRIORITY_HIGH
        mBuilder.setOnlyAlertOnce(true)
        mBuilder.setCategory(NotificationCompat.CATEGORY_ALARM)
        mBuilder.build().flags = Notification.PRIORITY_HIGH
        mBuilder.setContentIntent(pendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "channel_id"
            val channel =
                NotificationChannel(channelId, "channel name", NotificationManager.IMPORTANCE_HIGH)
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
            mBuilder.setChannelId(channelId)
        }
        val notification: Notification = mBuilder.build()
        notificationManager.notify(id, notification)
    }

    private fun createAlarm(context: Context, intent: Intent) {
        Log.d("aaa", "alarm")
        val bundle: Bundle = intent.extras ?: Bundle()
        val id = bundle.getInt(Constants.KEY_TASK_ID)
        val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
            putExtra(
                Constants.KEY_TASK_TITLE, bundle.getString(
                    Constants.KEY_TASK_TITLE,
                    context.getString(R.string.incoming_task)
                )
            )
            putExtra(
                Constants.KEY_TASK_TIME, DateTimeUtils.getShortTimeFromMillisecond(
                    bundle.getLong(
                        Constants.KEY_TASK_TIME,
                        System.currentTimeMillis()
                    )
                )
            )
            putExtra(
                Constants.KEY_SCREEN_LOCK_ENABLED,
                bundle.getBoolean(
                    Constants.KEY_SCREEN_LOCK_ENABLED,
                    false
                )
            )
            putExtra(Constants.KEY_TASK_ID, id)
        }

        val intent1 = Intent(context, TaskDetailActivity::class.java)
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent1.putExtra(Constants.KEY_TASK_ID, id)

        val pendingIntentContent =
            PendingIntent.getActivity(context, id, intent1, PendingIntent.FLAG_MUTABLE)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                id,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_time_outline)
                .setContentTitle(
                    bundle.getString(
                        Constants.KEY_TASK_TITLE,
                        context.getString(R.string.incoming_task)
                    )
                )
                .setContentText(
                    DateTimeUtils.getShortTimeFromMillisecond(
                        bundle.getLong(
                            Constants.KEY_TASK_TIME,
                            System.currentTimeMillis()
                        )
                    )
                )
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntentContent)
                .setFullScreenIntent(pendingIntent, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "CHANNEL_ID"
            val channel =
                NotificationChannel(channelId, "channel name", NotificationManager.IMPORTANCE_HIGH)
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
            notificationBuilder.setChannelId(channelId)
        }
        val notification: Notification =
            notificationBuilder.build().apply { flags = NotificationCompat.PRIORITY_HIGH }
        notificationManager.notify(id, notification)
    }
}
