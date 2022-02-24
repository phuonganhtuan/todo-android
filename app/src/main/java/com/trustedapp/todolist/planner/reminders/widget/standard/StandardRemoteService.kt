package com.trustedapp.todolist.planner.reminders.widget.standard

import android.content.Intent
import android.widget.RemoteViewsService

class StandardRemoteService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): StandardTodayFactory {
        return StandardTodayFactory(this.applicationContext, intent)
    }
}
