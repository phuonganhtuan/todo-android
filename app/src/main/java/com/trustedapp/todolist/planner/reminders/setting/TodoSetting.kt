package com.trustedapp.todolist.planner.reminders.setting

object DefaultSetting {

    // First day of week
    val firstDayOfWeek = FirstDayOfWeek.AUTO.name

    // Time format
    val timeFormat = TimeFormat.DEFAULT.name

    // Date format
    val dateFormat = DateFormat.DDMMYYYY.name
}

enum class TimeFormat {
    DEFAULT, H12, H24
}

enum class DateFormat {
    YYYYDDMM, DDMMYYYY, MMDDYYYY
}

enum class FirstDayOfWeek {
    AUTO, MONDAY, SATURDAY, SUNDAY
}
