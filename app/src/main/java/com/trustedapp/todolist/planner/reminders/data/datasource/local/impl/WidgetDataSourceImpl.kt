package com.trustedapp.todolist.planner.reminders.data.datasource.local.impl

import com.trustedapp.todolist.planner.reminders.data.datasource.local.dao.TaskDao
import com.trustedapp.todolist.planner.reminders.data.datasource.local.datasource.WidgetDataSource
import com.trustedapp.todolist.planner.reminders.data.models.entity.CountDownWidgetModel
import com.trustedapp.todolist.planner.reminders.data.models.entity.MonthWidgetModel
import com.trustedapp.todolist.planner.reminders.data.models.entity.StandardWidgetModel

class WidgetDataSourceImpl(private val dao: TaskDao) : WidgetDataSource {

    override fun getCountDownModel(widgetId: Int) = dao.getCountDownModel(widgetId)

    override suspend fun updateCountDownModel(entity: CountDownWidgetModel) =
        dao.updateCountDownModel(entity)

    override suspend fun deleteCountDownModel(entity: CountDownWidgetModel) =
        dao.deleteCountDownModel(entity)

    override suspend fun insertCountDownModel(entity: CountDownWidgetModel) =
        dao.insertCountDownModel(entity)

    override fun getMonthWidgetModel(widgetId: Int) = dao.getMonthWidgetModel(widgetId)

    override suspend fun updateMonthWidgetModel(entity: MonthWidgetModel) =
        dao.updateMonthWidgetModel(entity)

    override suspend fun deleteMonthWidgetModel(entity: MonthWidgetModel) =
        dao.deleteMonthWidgetModel(entity)

    override suspend fun insertMonthWidgetModel(entity: MonthWidgetModel) =
        dao.insertMonthWidgetModel(entity)

    override fun getStandardWidgetModel(widgetId: Int) = dao.getStandardWidgetModel(widgetId)

    override suspend fun deleteStandardWidgetModel(entity: StandardWidgetModel) =
        dao.deleteStandardWidgetModel(entity)

    override suspend fun updateStandardWidgetModel(entity: StandardWidgetModel) =
        dao.updateStandardWidgetModel(entity)

    override suspend fun insertStandardWidgetModel(entity: StandardWidgetModel) =
        dao.insertStandardWidgetModel(entity)
}
