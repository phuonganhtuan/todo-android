package com.trustedapp.todolist.planner.reminders.data.datasource.local.impl

import com.trustedapp.todolist.planner.reminders.data.datasource.local.dao.TaskDao
import com.trustedapp.todolist.planner.reminders.data.datasource.local.datasource.WidgetDataSource
import com.trustedapp.todolist.planner.reminders.data.models.entity.CountDownWidgetModel

class WidgetDataSourceImpl(private val dao: TaskDao) : WidgetDataSource {

    override fun getCountDownModel(widgetId: Int) = dao.getCountDownModel(widgetId)

    override suspend fun updateCountDownModel(entity: CountDownWidgetModel) =
        dao.updateCountDownModel(entity)

    override suspend fun deleteCountDownModel(entity: CountDownWidgetModel) =
        dao.deleteCountDownModel(entity)

    override suspend fun insertCountDownModel(entity: CountDownWidgetModel) =
        dao.insertCountDownModel(entity)
}
