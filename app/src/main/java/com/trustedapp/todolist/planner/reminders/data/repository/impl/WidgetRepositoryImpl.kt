package com.trustedapp.todolist.planner.reminders.data.repository.impl

import com.trustedapp.todolist.planner.reminders.data.datasource.local.datasource.WidgetDataSource
import com.trustedapp.todolist.planner.reminders.data.models.entity.CountDownWidgetModel
import com.trustedapp.todolist.planner.reminders.data.repository.WidgetRepository

class WidgetRepositoryImpl(private val dataSource: WidgetDataSource) : WidgetRepository {

    override fun getCountDownModel(widgetId: Int) = dataSource.getCountDownModel(widgetId)

    override suspend fun updateCountDownModel(entity: CountDownWidgetModel) =
        dataSource.updateCountDownModel(entity)

    override suspend fun deleteCountDownModel(entity: CountDownWidgetModel) =
        dataSource.deleteCountDownModel(entity)

    override suspend fun insertCountDownModel(entity: CountDownWidgetModel) =
        dataSource.insertCountDownModel(entity)
}
