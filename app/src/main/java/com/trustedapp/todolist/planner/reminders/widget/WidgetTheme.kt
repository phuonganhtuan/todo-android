package com.trustedapp.todolist.planner.reminders.widget

import com.trustedapp.todolist.planner.reminders.R

val widgetBgs = listOf(
    R.drawable.bg_widget_1,
    R.drawable.bg_widget_2,
    R.drawable.bg_widget_3,
    R.drawable.bg_widget_4,
    R.drawable.bg_widget_5,
    R.drawable.bg_widget_6,
    R.drawable.bg_widget_7,
    R.drawable.bg_widget_8,
    R.drawable.bg_widget_9,
    R.drawable.bg_widget_10,
    R.drawable.bg_widget_11,
)

val widgetBgColors = listOf(
    R.color.color_widget_bg_1,
    R.color.color_widget_bg_2,
    R.color.color_widget_bg_3,
    R.color.color_widget_bg_4,
    R.color.color_widget_bg_5,
    R.color.color_widget_bg_6,
    R.color.color_widget_bg_7,
    R.color.color_widget_bg_8,
    R.color.color_widget_bg_9,
    R.color.color_widget_bg_10,
    R.color.color_widget_bg_11,
)

fun isWidgetLightContent(color: Int) = listOf(
    widgetBgColors[1],
    widgetBgColors[2],
    widgetBgColors[3],
    widgetBgColors[5],
    widgetBgColors[6],
    widgetBgColors[7],
    widgetBgColors[8],
    widgetBgColors[9],
    widgetBgColors[10],
).contains(color)
