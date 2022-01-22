package com.example.todo.utils

import java.util.*

object DateTimeUtils {

    fun getTomorrow(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        return calendar.timeInMillis
    }
}
