package com.trustedapp.todolist.planner.reminders.utils

import android.content.Context
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.setting.DateFormat
import com.trustedapp.todolist.planner.reminders.setting.FirstDayOfWeek
import com.trustedapp.todolist.planner.reminders.setting.TimeFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*

object DateTimeUtils {

    lateinit var applicationContext: Context

    fun getTomorrow(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        calendar.set(HOUR_OF_DAY, 0)
        calendar.set(MINUTE, 0)
        return calendar.timeInMillis
    }

    fun getShortTimeFromDate(date: Date): String {
        val simpleDate = SimpleDateFormat("${getDayMonthFormat()} ${getTimeFormat()}")
        return simpleDate.format(date)
    }

    fun getShortTimeFromMillisecond(milis: Long): String {
        val date = Date().apply { time = milis }
        val simpleDate = SimpleDateFormat("${getDayMonthFormat()} ${getTimeFormat()}")
        return simpleDate.format(date)
    }

    fun getHourMinuteFromMillisecond(milis: Long): String {
        val date = Date().apply { time = milis }
        val simpleDate = SimpleDateFormat(getTimeFormat())
        return simpleDate.format(date)
    }

    fun getDayMonthFromMillisecond(milis: Long): String {
        val date = Date().apply { time = milis }
        val simpleDate = SimpleDateFormat(getDayMonthFormat())
        return simpleDate.format(date)
    }

    fun getDaysOfMonth(month: Calendar): List<Date> {

        val firstDayOfWeekSetting = SPUtils.getFirstDayOfWeek(applicationContext)

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
        val firstDayOfWeek = getInstance().apply { time = firstDate }.get(DAY_OF_WEEK)
        var daysOfLastMonthNum = firstDayOfWeek - 1
        when (firstDayOfWeekSetting) {
            FirstDayOfWeek.SATURDAY.name -> daysOfLastMonthNum += 1
            FirstDayOfWeek.MONDAY.name -> daysOfLastMonthNum -= 1
        }
        val daysOfNextMonthNum = 42 - daysInMonth - daysOfLastMonthNum

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

    fun getMonthYearString(month: Calendar): String {
        val year = month.get(YEAR)
        return "${getMonthInString(applicationContext, month)} $year"
    }

    fun getComparableDateString(
        date: Date,
        format: String? = null,
        isDefault: Boolean = false
    ): String {
        val dateFormat = SimpleDateFormat(
            if (isDefault) DATE_FORMAT_TYPE_2 else
                if (format == null) getDateFormat() else format
        )
        return dateFormat.format(date)
    }

    // Not visible to user
    fun getComparableMonthString(date: Date): String {
        val dateFormat = SimpleDateFormat("MM/yyyy")
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

    fun compareInMonth(date: Calendar, month: Calendar): Int {
        val year1 = date.get(YEAR)
        val year2 = month.get(YEAR)
        val month1 = date.get(MONTH)
        val month2 = month.get(MONTH)
        return when {
            year1 == year2 -> when {
                month1 == month2 -> 0
                month1 > month2 -> 1
                else -> -1
            }
            year1 > year2 -> 1
            else -> -1
        }
    }

    fun getCalendarDayTitle() = when (SPUtils.getFirstDayOfWeek(applicationContext)) {
        FirstDayOfWeek.AUTO.name -> listOf("S", "M", "T", "W", "T", "F", "S")
        FirstDayOfWeek.MONDAY.name -> listOf("M", "T", "W", "T", "F", "S", "S")
        FirstDayOfWeek.SATURDAY.name -> listOf("S", "S", "M", "T", "W", "T", "F")
        FirstDayOfWeek.SUNDAY.name -> listOf("S", "M", "T", "W", "T", "F", "S")
        else -> listOf("S", "M", "T", "W", "T", "F", "S")
    }

    private fun getTimeFormat() = when (SPUtils.getTimeFormat(applicationContext)) {
        TimeFormat.DEFAULT.name -> {
            if (android.text.format.DateFormat.is24HourFormat(applicationContext)) "HH:mm" else "hh:mm a"
        }
        TimeFormat.H12.name -> "hh:mm a"
        TimeFormat.H24.name -> "HH:mm"
        else -> "HH:mm"
    }

    private fun getDayMonthFormat() = when (SPUtils.getDateFormat(applicationContext)) {
        DateFormat.YYYYDDMM.name -> "dd/MM"
        DateFormat.DDMMYYYY.name -> "dd/MM"
        DateFormat.MMDDYYYY.name -> "MM/dd"
        else -> "dd/MM"
    }

    private fun getDateFormat() = when (SPUtils.getDateFormat(applicationContext)) {
        DateFormat.YYYYDDMM.name -> DATE_FORMAT_TYPE_1
        DateFormat.DDMMYYYY.name -> DATE_FORMAT_TYPE_2
        DateFormat.MMDDYYYY.name -> DATE_FORMAT_TYPE_3
        else -> DATE_FORMAT_TYPE_2
    }

    const val DATE_FORMAT_TYPE_1 = "yyyy/MM/dd"
    const val DATE_FORMAT_TYPE_2 = "dd/MM/yyyy"
    const val DATE_FORMAT_TYPE_3 = "MM/dd/yyyy"
}
