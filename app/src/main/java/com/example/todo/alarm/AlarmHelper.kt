package com.example.todo.alarm

import android.annotation.SuppressLint
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
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.todo.R
import com.example.todo.screens.home.HomeActivity


class AlarmHelper : BroadcastReceiver() {

//    override fun onReceive(p0: Context?, p1: Intent?) {
//        Log.d("aaa", "notified")
//    }


    // For testing, will be updated soon
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("aaa", "notified")
        val bundle: Bundle = intent.extras ?: Bundle()
        val text = bundle.getString("event")
//        val date = bundle.getString("date") + " " + bundle.getString("time")
        //Click on Notification
        //Click on Notification
        val intent1 = Intent(context, HomeActivity::class.java)
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent1.putExtra("message", text)
        //Notification Builder
        //Notification Builder
        val pendingIntent =
            PendingIntent.getActivity(context, 1, intent1, PendingIntent.FLAG_ONE_SHOT)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(context, "notify_001")
        //here we set all the properties for the notification
        //here we set all the properties for the notification
        val contentView = RemoteViews(context.packageName, R.layout.layout_notification)
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher)
        val pendingSwitchIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        contentView.setOnClickPendingIntent(R.id.layoutNoti, pendingSwitchIntent)
        contentView.setTextViewText(R.id.message, text)
//        contentView.setTextViewText(R.id.date, date)
        mBuilder.setSmallIcon(R.drawable.ic_date_outline)
        mBuilder.setAutoCancel(true)
        mBuilder.setOngoing(true)
        mBuilder.setAutoCancel(true)
        mBuilder.priority = Notification.PRIORITY_HIGH
        mBuilder.setOnlyAlertOnce(true)
        mBuilder.build().flags = Notification.FLAG_NO_CLEAR or Notification.PRIORITY_HIGH
        mBuilder.setContent(contentView)
        mBuilder.setContentIntent(pendingIntent)
        //we have to create notification channel after api level 26
        //we have to create notification channel after api level 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "channel_id"
            val channel =
                NotificationChannel(channelId, "channel name", NotificationManager.IMPORTANCE_HIGH)
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
            mBuilder.setChannelId(channelId)
        }
        val notification: Notification = mBuilder.build()
        notificationManager.notify(1, notification)
    }
}
