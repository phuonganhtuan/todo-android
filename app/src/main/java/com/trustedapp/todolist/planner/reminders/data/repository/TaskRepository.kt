package com.trustedapp.todolist.planner.reminders.data.repository

import com.trustedapp.todolist.planner.reminders.data.models.entity.*
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getShortTasks(): Flow<List<TaskShort>>
    fun getTask(id: Int): Task
    fun getCategories(): Flow<List<CategoryEntity>>
    suspend fun addTask(entity: TaskEntity): Long
    suspend fun addTaskDetail(entity: TaskDetailEntity)
    suspend fun addSubTasks(entity: SubTaskEntity)
    suspend fun addAttachment(entity: AttachmentEntity)
    suspend fun addBookmark(entity: BookmarkEntity): Long
    suspend fun addCategory(entity: CategoryEntity): Long
    suspend fun addReminder(entity: ReminderEntity): Long

    suspend fun updateTask(entity: TaskEntity)
    suspend fun updateCategory(entity: CategoryEntity): Int
    suspend fun updateTaskDetail(entity: TaskDetailEntity): Int
    suspend fun updateSubTask(entity: SubTaskEntity): Int
    suspend fun updateAttachment(entity: AttachmentEntity): Int
    suspend fun updateReminder(entity: ReminderEntity): Int

    suspend fun deleteCategories()
    suspend fun deleteTasks()
    suspend fun deleteTaskDetails()
    suspend fun deleteBookmarks()
    suspend fun deleteAttachments()
    suspend fun deleteSubtasks()

    fun searchTaskByName(name: String): List<TaskEntity>
    fun getTaskInDay(dayString: String): List<TaskShort>

    suspend fun deleteAttachment(id: Int)
    suspend fun deleteSubtask(id: Int)
    suspend fun deleteTaskDetail(id: Int)
    suspend fun deleteTask(id: Int)

    fun getBookmarks(): Flow<List<BookmarkEntity>>
    fun getReminder(taskId: Int): ReminderEntity?
    suspend fun deleteReminder(taskId: Int)
}
