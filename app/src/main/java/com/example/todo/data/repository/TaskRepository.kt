package com.example.todo.data.repository

import com.example.todo.data.models.entity.*
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getShortTasks(): Flow<List<TaskShort>>
    fun getTask(id: Int): Flow<Task>
    suspend fun addTask(entity: TaskEntity): Long
    suspend fun addTaskDetail(entity: TaskDetailEntity)
    suspend fun addSubTasks(entity: SubTaskEntity)
    suspend fun addAttachment(entity: AttachmentEntity)
    suspend fun addBookmark(entity: BookmarkEntity): Long
    suspend fun addCategory(entity: CategoryEntity): Long

    // for testing
    suspend fun deleteCategories()
    suspend fun deleteTasks()
    suspend fun deleteTaskDetails()
    suspend fun deleteBookmarks()
    suspend fun deleteAttachments()
    suspend fun deleteSubtasks()
}
