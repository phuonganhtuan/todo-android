package com.trustedapp.todolist.planner.reminders.widget.month

import android.content.Intent
import android.widget.RemoteViewsService

class MonthRemoteService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): MonthTodayFactory {
        return MonthTodayFactory(this.applicationContext, intent)
    }
}
