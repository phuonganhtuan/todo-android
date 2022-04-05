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

    @Transaction
    @Query("select * from TaskEntity where isDone = 0 and calendar >= :currentTime")
    fun getTasksForAlarm(currentTime: Long): List<Task>

    @Transaction
    @Query("select * from TaskEntity where calendar > :dayTime and isDone = 0")
    fun getFutureTask(dayTime: Long): List<TaskShort>

    @Transaction
    @Query("select * from TaskEntity where calendar > :dayTime")
    fun getFutureTaskAll(dayTime: Long): List<TaskShort>

    @Transaction
    @Query("select * from TaskEntity where id = :id limit 1")
    fun getTask(id: Int): Task

    @Query("select * from CategoryEntity")
    fun getCategories(): Flow<List<CategoryEntity>>

    @Query("select * from CategoryEntity where id = :id limit 1")
    fun getCategory(id: Int): CategoryEntity?

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
    fun updateTaskNoSuspend(entity: TaskEntity)

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

    @Transaction
    @Query("select * from TaskEntity where (dueDate == :dayString or (calendar > :start and calendar <= :end)) and isDone = 0")
    fun getTaskInDay(dayString: String, start: Long, end: Long): List<TaskShort>

    @Transaction
    @Query("select * from TaskEntity where (dueDate == :dayString or (calendar > :start and calendar <= :end))")
    fun getTaskInDayAll(dayString: String, start: Long, end: Long): List<TaskShort>

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

    @Query("select * from CountDownWidgetModel where widgetId = :widgetId")
    fun getCountDownModel(widgetId: Int): CountDownWidgetModel?

    @Delete
    suspend fun deleteCountDownModel(entity: CountDownWidgetModel)

    @Update(onConflict = REPLACE)
    suspend fun updateCountDownModel(entity: CountDownWidgetModel)

    @Insert(onConflict = REPLACE)
    suspend fun insertCountDownModel(entity: CountDownWidgetModel)

    @Query("select * from MonthWidgetModel where widgetId = :widgetId")
    fun getMonthWidgetModel(widgetId: Int): MonthWidgetModel?

    @Delete
    suspend fun deleteMonthWidgetModel(entity: MonthWidgetModel)

    @Update(onConflict = REPLACE)
    suspend fun updateMonthWidgetModel(entity: MonthWidgetModel)

    @Insert(onConflict = REPLACE)
    suspend fun insertMonthWidgetModel(entity: MonthWidgetModel)

    @Query("select * from StandardWidgetModel where widgetId = :widgetId")
    fun getStandardWidgetModel(widgetId: Int): StandardWidgetModel?

    @Delete
    suspend fun deleteStandardWidgetModel(entity: StandardWidgetModel)

    @Update(onConflict = REPLACE)
    suspend fun updateStandardWidgetModel(entity: StandardWidgetModel)

    @Insert(onConflict = REPLACE)
    suspend fun insertStandardWidgetModel(entity: StandardWidgetModel)
}
