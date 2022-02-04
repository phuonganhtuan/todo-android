package com.example.todo.utils

import android.content.Context
import com.example.todo.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.util.*
import java.util.Calendar.*

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

    fun getHourMinuteFromMillisecond(milis: Long): String {
        val date = Date().apply { time = milis }
        val simpleDate = SimpleDateFormat("hh:mm")
        return simpleDate.format(date)
    }

    fun getDaysOfMonth(month: Calendar, startFromMonday: Boolean = false): List<Date> {

        val result = mutableListOf<Date>()
        val copiedMonth = getInstance().apply { time = month.time }
        copiedMonth.set(MONTH, copiedMonth.get(MONTH))
        copiedMonth.set(DAY_OF_MONTH, 1)

        val nextMonth = getInstance().apply { time = month.time }
        nextMonth.set(MONTH, nextMonth.get(MONTH) + 1)
        nextMonth.set(DAY_OF_MONTH, 1)

        val daysInMonth = copiedMonth.getActualMaximum(DAY_OF_MONTH)

        for (i in 0 until (daysInMonth)) {
            result.add(copiedMonth.time)
            copiedMonth.add(DAY_OF_MONTH, 1)
        }

        val firstDate = result.first()
        var firstDayOfWeek = getInstance().apply { time = firstDate }.get(DAY_OF_WEEK)
        if (startFromMonday) firstDayOfWeek -= 1
        val daysOfLastMonthNum = firstDayOfWeek - 1
        val daysOfNextMonthNum = (7 - ((daysInMonth + daysOfLastMonthNum) % 7)) % 7

        val copiedMonth2 = getInstance().apply { time = month.time }
        copiedMonth2.set(MONTH, copiedMonth2.get(MONTH))
        copiedMonth2.set(DAY_OF_MONTH, 1)

        for (i in 0 until daysOfLastMonthNum) {
            copiedMonth2.add(DAY_OF_MONTH, -1)
            result.add(0, copiedMonth2.time)
        }

        for (i in 0 until daysOfNextMonthNum) {
            result.add(nextMonth.time)
            nextMonth.add(DAY_OF_MONTH, 1)
        }
        return result
    }

    fun getMonthYearString(context: Context, month: Calendar): String {
        val year = month.get(YEAR)
        return "${getMonthInString(context, month)} $year"
    }

    fun getComparableDateString(date: Date): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        return dateFormat.format(date)
    }

    private fun getMonthInString(context: Context, month: Calendar): String {

        val stringId = when (month.get(MONTH) + 1) {
            1 -> R.string.jan
            2 -> R.string.feb
            3 -> R.string.mar
            4 -> R.string.apr
            5 -> R.string.may
            6 -> R.string.jun
            7 -> R.string.jul
            8 -> R.string.aug
            9 -> R.string.sep
            10 -> R.string.oct
            11 -> R.string.nov
            12 -> R.string.dec
            else -> return ""
        }
        return context.getString(stringId)
    }

    private fun getLastDayOfMonth(month: Calendar): Int {
        month[Calendar.DATE] = month.getActualMaximum(Calendar.DATE)
        return month.get(DAY_OF_MONTH)
    }
}
