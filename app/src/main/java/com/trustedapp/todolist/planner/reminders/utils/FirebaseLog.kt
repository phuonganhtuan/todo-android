package com.trustedapp.todolist.planner.reminders.utils

import android.os.Bundle
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class FirebaseLog {
    companion object {
        const val EVENT_TASK_SCREEN = "Tasks"
        const val EVENT_CALENDAR_SCREEN = "Calendar"
        const val EVENT_MINE_SCREEN = "Mine"
        const val EVENT_TODAY_IN_TASK_SCREEN = "Today"
        const val EVENT_FUTURE_IN_TASK_SCREEN = "Future"
        const val EVENT_COMPLETE_IN_TASK_SCREEN = "Completed"
        const val EVENT_CALENDAR_IN_CALENDAR_SCREEN = "Table_calendar"
        const val EVENT_CLICK_DONE = "Mark"

        fun logAnalytics(name: String, bundleData: Bundle) {
            Firebase.analytics.logEvent(name, bundleData)
        }

        fun logEventTaskScreen(){
            Firebase.analytics.logEvent(EVENT_TASK_SCREEN, Bundle())
        }

        fun logEventCalendarScreen(){
            Firebase.analytics.logEvent(EVENT_CALENDAR_SCREEN, Bundle())
        }

        fun logEventMineScreen(){
            Firebase.analytics.logEvent(EVENT_MINE_SCREEN, Bundle())
        }

        fun logEventTodayInTaskScreen(){
            Firebase.analytics.logEvent(EVENT_TODAY_IN_TASK_SCREEN, Bundle())
        }

        fun logEventFutureInTaskScreen(){
            Firebase.analytics.logEvent(EVENT_FUTURE_IN_TASK_SCREEN, Bundle())
        }

        fun logEventCompleteInTaskScreen(){
            Firebase.analytics.logEvent(EVENT_COMPLETE_IN_TASK_SCREEN, Bundle())
        }

        fun logEventCalendarInCalendarScreen(){
            Firebase.analytics.logEvent(EVENT_CALENDAR_IN_CALENDAR_SCREEN, Bundle())
        }

        fun logEventClickDone(){
            Firebase.analytics.logEvent(EVENT_CLICK_DONE, Bundle())
        }
    }
}