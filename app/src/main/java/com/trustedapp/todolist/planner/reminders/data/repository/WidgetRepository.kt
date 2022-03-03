package com.trustedapp.todolist.planner.reminders.data.repository

import com.trustedapp.todolist.planner.reminders.data.models.entity.CountDownWidgetModel
import com.trustedapp.todolist.planner.reminders.data.models.entity.MonthWidgetModel
import com.trustedapp.todolist.planner.reminders.data.models.entity.StandardWidgetModel
import com.trustedapp.todolist.planner.reminders.data.models.entity.TaskShort

interface WidgetRepository {

    fun getCountDownModel(widgetId: Int): CountDownWidgetModel?
    suspend fun deleteCountDownModel(entity: CountDownWidgetModel)
    suspend fun updateCountDownModel(entity: CountDownWidgetModel)
    suspend fun insertCountDownModel(entity: CountDownWidgetModel)

    fun getMonthWidgetModel(widgetId: Int): MonthWidgetModel?
    suspend fun deleteMonthWidgetModel(entity: MonthWidgetModel)
    suspend fun updateMonthWidgetModel(entity: MonthWidgetModel)
    suspend fun insertMonthWidgetModel(entity: MonthWidgetModel)

    fun getStandardWidgetModel(widgetId: Int): StandardWidgetModel?
    suspend fun deleteStandardWidgetModel(entity: StandardWidgetModel)
    suspend fun updateStandardWidgetModel(entity: StandardWidgetModel)
    suspend fun insertStandardWidgetModel(entity: StandardWidgetModel)
}
