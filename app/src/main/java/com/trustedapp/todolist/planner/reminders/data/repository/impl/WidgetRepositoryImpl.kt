package com.trustedapp.todolist.planner.reminders.data.repository.impl

import com.trustedapp.todolist.planner.reminders.data.datasource.local.datasource.WidgetDataSource
import com.trustedapp.todolist.planner.reminders.data.models.entity.CountDownWidgetModel
import com.trustedapp.todolist.planner.reminders.data.models.entity.MonthWidgetModel
import com.trustedapp.todolist.planner.reminders.data.models.entity.StandardWidgetModel
import com.trustedapp.todolist.planner.reminders.data.repository.WidgetRepository

class WidgetRepositoryImpl(private val dataSource: WidgetDataSource) : WidgetRepository {

    override fun getCountDownModel(widgetId: Int) = dataSource.getCountDownModel(widgetId)

    override suspend fun updateCountDownModel(entity: CountDownWidgetModel) =
        dataSource.updateCountDownModel(entity)

    override suspend fun deleteCountDownModel(entity: CountDownWidgetModel) =
        dataSource.deleteCountDownModel(entity)

    override suspend fun insertCountDownModel(entity: CountDownWidgetModel) =
        dataSource.insertCountDownModel(entity)

    override fun getMonthWidgetModel(widgetId: Int) = dataSource.getMonthWidgetModel(widgetId)

    override suspend fun updateMonthWidgetModel(entity: MonthWidgetModel) =
        dataSource.updateMonthWidgetModel(entity)

    override suspend fun deleteMonthWidgetModel(entity: MonthWidgetModel) =
        dataSource.deleteMonthWidgetModel(entity)

    override suspend fun insertMonthWidgetModel(entity: MonthWidgetModel) =
        dataSource.insertMonthWidgetModel(entity)

    override fun getStandardWidgetModel(widgetId: Int) = dataSource.getStandardWidgetModel(widgetId)

    override suspend fun deleteStandardWidgetModel(entity: StandardWidgetModel) =
        dataSource.deleteStandardWidgetModel(entity)

    override suspend fun updateStandardWidgetModel(entity: StandardWidgetModel) =
        dataSource.updateStandardWidgetModel(entity)

    override suspend fun insertStandardWidgetModel(entity: StandardWidgetModel) =
        dataSource.insertStandardWidgetModel(entity)
}
