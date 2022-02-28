package com.trustedapp.todolist.planner.reminders

import android.app.Application
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DemoApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        DateTimeUtils.applicationContext = applicationContext
    }
}
