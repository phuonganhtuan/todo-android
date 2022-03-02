package com.trustedapp.todolist.planner.reminders.data.repository

import com.trustedapp.todolist.planner.reminders.data.models.entity.CountDownWidgetModel

interface WidgetRepository {

    fun getCountDownModel(widgetId: Int): CountDownWidgetModel?
    suspend fun deleteCountDownModel(entity: CountDownWidgetModel)
    suspend fun updateCountDownModel(entity: CountDownWidgetModel)
    suspend fun insertCountDownModel(entity: CountDownWidgetModel)
}
