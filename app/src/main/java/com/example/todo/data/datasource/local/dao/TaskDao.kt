package com.example.todo.data.datasource.local.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import com.example.todo.data.models.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Transaction
    @Query("select * from TaskEntity")
    fun getShortTasks(): Flow<List<TaskShort>>

    @Transaction
    @Query("select * from TaskEntity where id = :id limit 1")
    fun getTask(id: Int): Flow<Task>

    @Query("select * from CategoryEntity")
    fun getCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = REPLACE)
    suspend fun addTask(entity: TaskEntity): Long

    @Insert(onConflict = REPLACE)
    suspend fun addTaskDetail(entity: TaskDetailEntity)

    @Insert(onConflict = REPLACE)
    suspend fun addSubTasks(entity: SubTaskEntity)

    @Insert(onConflict = REPLACE)
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

    @Query("select * from TaskEntity where dueDate == :dayString")
    fun getTaskInDay(dayString: String): List<TaskShort>
}
