package com.example.todo.data.repository.impl

import com.example.todo.data.datasource.local.datasource.TaskLocalDataSource
import com.example.todo.data.models.entity.Task
import com.example.todo.data.repository.TaskRepository
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDataSource: TaskLocalDataSource,
) : TaskRepository {

    override fun getShortTasks() = taskDataSource.getShortTasks()
    override fun getTask(id: Int) = taskDataSource.getTask(id)
    override suspend fun addTask(entity: Task) = taskDataSource.addTask(entity)
    override suspend fun updateTask(entity: Task) = taskDataSource.updateTask(entity)
    override suspend fun deleteTask(entity: Task) = taskDataSource.deleteTask(entity)
}
