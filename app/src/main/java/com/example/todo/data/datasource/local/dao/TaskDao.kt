package com.example.todo.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.todo.data.models.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("select * from TaskEntity")
    fun getShortTasks(): Flow<List<TaskShort>>

    @Query("select * from TaskEntity where id = :id limit 1")
    fun getTask(id: Int): Flow<Task>

    @Insert(onConflict = REPLACE)
    suspend fun addTask(entity: TaskEntity): Long

    @Insert(onConflict = REPLACE)
    suspend fun addTaskDetail(entity: TaskDetailEntity)

    @Insert(onConflict = REPLACE)
    suspend fun addSubTasks(entity: SubTaskEntity)

    @Insert(onConflict = REPLACE)
    suspend fun addAttachment(entity: AttachmentEntity)

    @Insert(onConflict = REPLACE)
    suspend fun addBookmark(entity: BookmarkEntity)

    @Insert(onConflict = REPLACE)
    suspend fun addCategory(entity: CategoryEntity): Long
}
