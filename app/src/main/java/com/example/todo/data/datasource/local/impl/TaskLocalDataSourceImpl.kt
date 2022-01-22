package com.example.todo.data.datasource.local.impl

import com.example.todo.data.datasource.local.dao.TaskDao
import com.example.todo.data.datasource.local.datasource.TaskLocalDataSource
import com.example.todo.data.models.entity.Task
import javax.inject.Inject

class TaskLocalDataSourceImpl @Inject constructor(private val dao: TaskDao) : TaskLocalDataSource {

    override fun getShortTasks() = dao.getShortTasks()
    override fun getTask(id: Int) = dao.getTask(id)
    override suspend fun addTask(entity: Task) = dao.addTask(entity)
    override suspend fun updateTask(entity: Task) = dao.updateTask(entity)
    override suspend fun deleteTask(entity: Task) = dao.deleteTask(entity)
}
