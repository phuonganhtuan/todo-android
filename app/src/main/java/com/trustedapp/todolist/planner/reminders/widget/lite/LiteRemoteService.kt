package com.trustedapp.todolist.planner.reminders.widget.lite

import android.content.Intent
import android.widget.RemoteViewsService

class LiteRemoteService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): LiteTodayFactory {
        return LiteTodayFactory(this.applicationContext, intent)
    }
}
