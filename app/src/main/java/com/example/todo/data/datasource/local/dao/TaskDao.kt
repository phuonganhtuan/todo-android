package com.example.todo.data.datasource.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.example.todo.data.models.entity.Task
import com.example.todo.data.models.entity.TaskShort
import kotlinx.coroutines.flow.Flow

interface TaskDao {

    @Query("select * from TaskEntity")
    fun getShortTasks(): Flow<List<TaskShort>>

    @Query("select * from TaskEntity where id = :id limit 1")
    fun getTask(id: Int): Flow<Task>

    @Insert(onConflict = REPLACE)
    suspend fun addTask(entity: Task)

    @Update(onConflict = REPLACE)
    suspend fun updateTask(entity: Task)

    @Delete
    suspend fun deleteTask(entity: Task)
}
