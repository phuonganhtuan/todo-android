package com.example.todo.common.chart

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.example.todo.R
import java.lang.String

object ChartColor {

    fun initChartColor(context: Context) {
        val colorPrimary = ContextCompat.getColor(context, R.color.color_primary)
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
