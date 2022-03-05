package com.trustedapp.todolist.planner.reminders.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.data.models.entity.TODO_DEFAULT_RINGTONE_ID
import com.trustedapp.todolist.planner.reminders.screens.newtask.ReminderTypeEnum
import com.trustedapp.todolist.planner.reminders.screens.taskdetail.TaskDetailActivity
import com.trustedapp.todolist.planner.reminders.utils.Constants
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import com.trustedapp.todolist.planner.reminders.utils.SPUtils


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

        val ringtone = SPUtils.getDefaultNotificationRingtone(context)
        val ringtoneUri = if (ringtone?.id == TODO_DEFAULT_RINGTONE_ID) {
            Uri.parse("android.resource://" + context.packageName + "/" + R.raw.to_do_default)
        } else {
            Uri.parse(ringtone?.ringtoneUri)
        }

        val mediaPlayer = MediaPlayer.create(context, ringtoneUri)
        mediaPlayer.setOnPreparedListener {
            Handler(Looper.getMainLooper()).postDelayed({
                it.stop()
            }, 2000)
        }
        mediaPlayer.start()

        val pendingIntent =
            PendingIntent.getActivity(context, id, intent1, PendingIntent.FLAG_MUTABLE)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, "channel_id_noti")
        builder.setSmallIcon(R.drawable.ic_time_outline)
        builder.setContentText(
            DateTimeUtils.getShortTimeFromMillisecond(
                bundle.getLong(
                    Constants.KEY_TASK_TIME,
                    System.currentTimeMillis()
                )
            )
        )
        builder.setContentTitle(
            bundle.getString(
                Constants.KEY_TASK_TITLE,
                context.getString(R.string.incoming_task)
            )
        )
        builder.setDefaults(NotificationCompat.DEFAULT_VIBRATE)
        builder.setAutoCancel(true)
        builder.priority = Notification.PRIORITY_HIGH
        builder.setOnlyAlertOnce(true)
        builder.setSound(null)
        builder.setVisibility(if (SPUtils.getIsScreenlockTaskReminder(context)) NotificationCompat.VISIBILITY_PUBLIC else NotificationCompat.VISIBILITY_SECRET)
        builder.setCategory(NotificationCompat.CATEGORY_ALARM)
        builder.build().flags = Notification.PRIORITY_MAX
        builder.setContentIntent(pendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "channel_id_noti"
            val channel =
                NotificationChannel(
                    channelId,
                    "channel name notification",
                    NotificationManager.IMPORTANCE_LOW
                )
            channel.enableVibration(true)
            channel.lockscreenVisibility =
                if (SPUtils.getIsScreenlockTaskReminder(context)) NotificationCompat.VISIBILITY_PUBLIC else NotificationCompat.VISIBILITY_SECRET
            notificationManager.createNotificationChannel(channel)
            builder.setChannelId(channelId)
        }
        val notification: Notification = builder.build()
        notificationManager.notify(0, notification)
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
                .setVisibility(if (SPUtils.getIsScreenlockTaskReminder(context)) NotificationCompat.VISIBILITY_PUBLIC else NotificationCompat.VISIBILITY_SECRET)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntentContent)
                .setFullScreenIntent(pendingIntent, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "CHANNEL_ID"
            val channel =
                NotificationChannel(channelId, "channel name", NotificationManager.IMPORTANCE_HIGH)
            channel.enableVibration(true)
            channel.lockscreenVisibility =
                if (SPUtils.getIsScreenlockTaskReminder(context)) NotificationCompat.VISIBILITY_PUBLIC else NotificationCompat.VISIBILITY_SECRET
            notificationManager.createNotificationChannel(channel)
            notificationBuilder.setChannelId(channelId)
        }
        val notification: Notification =
            notificationBuilder.build().apply { flags = NotificationCompat.PRIORITY_HIGH }
        notificationManager.notify(id, notification)
    }
}
