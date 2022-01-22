package com.example.todo.data.datasource.local.datasource

import com.example.todo.data.models.entity.Task
import com.example.todo.data.models.entity.TaskShort
import kotlinx.coroutines.flow.Flow

interface TaskLocalDataSource {

    fun getShortTasks(): Flow<List<TaskShort>>
    fun getTask(id: Int): Flow<Task>
    suspend fun addTask(entity: Task)
    suspend fun updateTask(entity: Task)
    suspend fun deleteTask(entity: Task)
}
