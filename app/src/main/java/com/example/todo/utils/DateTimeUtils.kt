package com.example.todo.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    fun getTomorrow(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        return calendar.timeInMillis
    }

    fun getShortTimeFromDate(date: Date): String {
        val simpleDate = SimpleDateFormat("dd/MM hh:mm")
        return simpleDate.format(date)
    }

    fun getShortTimeFromMillisecond(milis: Long): String {
        val date = Date().apply { time = milis }
        val simpleDate = SimpleDateFormat("dd/MM hh:mm")
        return simpleDate.format(date)
    }
}
