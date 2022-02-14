package com.trustedapp.todolist.planner.reminders.data.datasource.local.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import com.trustedapp.todolist.planner.reminders.data.models.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Transaction
    @Query("select * from TaskEntity")
    fun getShortTasks(): Flow<List<TaskShort>>

    @Query("select * from TaskEntity where isDone = 0 and calendar >= :currentTime")
    fun getTasksForAlarm(currentTime: Long): List<Task>

    @Transaction
    @Query("select * from TaskEntity where id = :id limit 1")
    fun getTask(id: Int): Task

    @Query("select * from CategoryEntity")
    fun getCategories(): Flow<List<CategoryEntity>>

    @Query("select * from ReminderEntity where taskId = :taskId limit 1")
    fun getReminder(taskId: Int): ReminderEntity?

    @Insert(onConflict = IGNORE)
    suspend fun addTask(entity: TaskEntity): Long

    @Insert(onConflict = IGNORE)
    suspend fun addReminder(entity: ReminderEntity): Long

    @Insert(onConflict = IGNORE)
    suspend fun addTaskDetail(entity: TaskDetailEntity)

    @Insert(onConflict = IGNORE)
    suspend fun addSubTasks(entity: SubTaskEntity)

    @Insert(onConflict = IGNORE)
    suspend fun addAttachment(entity: AttachmentEntity)

    @Insert(onConflict = REPLACE)
    suspend fun addBookmark(entity: BookmarkEntity): Long

    @Insert(onConflict = REPLACE)
    suspend fun addCategory(entity: CategoryEntity): Long

    @Update(onConflict = IGNORE)
    suspend fun updateTask(entity: TaskEntity)

    @Update(onConflict = IGNORE)
    suspend fun updateCategory(entity: CategoryEntity): Int

    @Update(onConflict = IGNORE)
    suspend fun updateSubTask(entity: SubTaskEntity): Int

    @Update(onConflict = IGNORE)
    suspend fun updateAttachment(entity: AttachmentEntity): Int

    @Update(onConflict = IGNORE)
    suspend fun updateTaskDetail(entity: TaskDetailEntity): Int

    @Update(onConflict = IGNORE)
    suspend fun updateReminder(entity: ReminderEntity): Int

    @Query("delete from CategoryEntity where 1")
    suspend fun deleteCategories()

    @Query("delete from TaskEntity where 1")
    suspend fun deleteTasks()

    @Query("delete from TaskDetailEntity where 1")
    suspend fun deleteTaskDetails()

    @Query("delete from BookmarkEntity where 1")
    suspend fun deleteBookmarks()

    @Query("delete from AttachmentEntity where 1")
    suspend fun deleteAttachments()

    @Query("delete from SubTaskEntity where 1")
    suspend fun deleteSubtasks()

    @Query("select * from TaskEntity where title like '%' || :name || '%'")
    fun searchTaskByName(name: String): List<TaskEntity>

    @Query("select * from TaskEntity where dueDate == :dayString and isDone = 0")
    fun getTaskInDay(dayString: String): List<TaskShort>

    @Query("delete from AttachmentEntity where id = :id")
    suspend fun deleteAttachment(id: Int)

    @Query("delete from SubTaskEntity where id = :id")
    suspend fun deleteSubtask(id: Int)

    @Query("delete from TaskDetailEntity where id = :id")
    suspend fun deleteTaskDetail(id: Int)

    @Query("delete from TaskEntity where id = :id")
    suspend fun deleteTask(id: Int)

    @Query("delete from ReminderEntity where taskId = :taskId")
    suspend fun deleteReminder(taskId: Int)

    @Query("select * from BookmarkEntity")
    fun getBookmarks(): Flow<List<BookmarkEntity>>
}