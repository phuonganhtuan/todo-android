package com.trustedapp.todolist.planner.reminders.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.data.models.entity.TODO_DEFAULT_RINGTONE_ID
import com.trustedapp.todolist.planner.reminders.screens.home.HomeActivity
import com.trustedapp.todolist.planner.reminders.utils.SPUtils

class DailyReminderBroadcast : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        p0?.let {
            createDailyNotification(it)
        }
    }

    private fun createDailyNotification(context: Context) {

        val id = -2
        val intent1 = Intent(context, HomeActivity::class.java)
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent =
            PendingIntent.getActivity(context, id, intent1, PendingIntent.FLAG_MUTABLE)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val ringtone = SPUtils.getDailyRingtone(context)
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
        val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        manager.setStreamVolume(AudioManager.STREAM_MUSIC, 10, 0)
        if (manager.ringerMode == AudioManager.RINGER_MODE_NORMAL) {
            mediaPlayer.start()
        }

        val builder = NotificationCompat.Builder(context, "channel_id_daily")
        builder.setSmallIcon(R.drawable.ic_app_icon)
        builder.setContentTitle(context.getString(R.string.app_name))
        builder.setContentText(context.getString(R.string.organize_tasks_easier_with_to_do_list))
        builder.setDefaults(NotificationCompat.DEFAULT_VIBRATE)
        builder.setAutoCancel(false)
        builder.priority = Notification.PRIORITY_HIGH
        builder.setOnlyAlertOnce(true)
        builder.setSound(null)
        builder.setVisibility(if (SPUtils.getIsScreenlockTaskReminder(context)) NotificationCompat.VISIBILITY_PUBLIC else NotificationCompat.VISIBILITY_SECRET)
        builder.setCategory(NotificationCompat.CATEGORY_ALARM)
        builder.build().flags = Notification.PRIORITY_HIGH
        builder.setContentIntent(pendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "channel_id_daily"
            val channel =
                NotificationChannel(
                    channelId,
                    "channel name notification",
                    NotificationManager.IMPORTANCE_HIGH
                )
            channel.enableVibration(true)
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
