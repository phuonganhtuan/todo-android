package com.trustedapp.todolist.planner.reminders.data.datasource.local.datasource

import com.trustedapp.todolist.planner.reminders.data.models.entity.CountDownWidgetModel

interface WidgetDataSource {

    fun getCountDownModel(widgetId: Int): CountDownWidgetModel?
    suspend fun deleteCountDownModel(entity: CountDownWidgetModel)
    suspend fun updateCountDownModel(entity: CountDownWidgetModel)
    suspend fun insertCountDownModel(entity: CountDownWidgetModel)
}
