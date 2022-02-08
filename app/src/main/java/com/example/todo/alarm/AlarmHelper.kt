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
import com.example.todo.R
import com.example.todo.screens.taskdetail.TaskDetailActivity
import com.example.todo.utils.Constants
import com.example.todo.utils.DateTimeUtils


class AlarmHelper : BroadcastReceiver() {

    // For testing, will be updated soon
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("aaa", "notified")
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
        mBuilder.setAutoCancel(false)
        mBuilder.setOngoing(true)
        mBuilder.priority = Notification.PRIORITY_HIGH
        mBuilder.setOnlyAlertOnce(true)
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
}
