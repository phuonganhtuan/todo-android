package com.trustedapp.todolist.planner.reminders.common.chart

import android.content.Context
import android.graphics.Color
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.utils.getColorFromAttr

object ChartColor {

    fun initChartColor(context: Context) {
        val colorPrimary = context.getColorFromAttr(R.attr.colorPrimary)
        val colorString = String.format("%06X", 0xFFFFFF and colorPrimary)
        chartColors = listOf(
            Color.parseColor("#${colorString}"),
            Color.parseColor("#80${colorString}"),
            Color.parseColor("#60${colorString}"),
            Color.parseColor("#40${colorString}"),
            Color.parseColor("#20${colorString}"),
            Color.parseColor("#10${colorString}")
        )
    }

    var chartColors = listOf(
        Color.parseColor("#22B07D"),
        Color.parseColor("#8022B07D"),
        Color.parseColor("#6022B07D"),
        Color.parseColor("#4022B07D"),
        Color.parseColor("#2022B07D"),
        Color.parseColor("#1022B07D")
    )
}
