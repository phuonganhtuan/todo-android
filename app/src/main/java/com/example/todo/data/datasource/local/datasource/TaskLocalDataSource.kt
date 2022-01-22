package com.example.todo.data.datasource.local.datasource

import com.example.todo.data.models.entity.*
import kotlinx.coroutines.flow.Flow

interface TaskLocalDataSource {

    fun getShortTasks(): Flow<List<TaskShort>>
    fun getTask(id: Int): Flow<Task>
    suspend fun addTask(entity: TaskEntity): Long
    suspend fun addTaskDetail(entity: TaskDetailEntity)
    suspend fun addSubTasks(entity: SubTaskEntity)
    suspend fun addAttachment(entity: AttachmentEntity)
    suspend fun addBookmark(entity: BookmarkEntity)
    suspend fun addCategory(entity: CategoryEntity): Long
}
