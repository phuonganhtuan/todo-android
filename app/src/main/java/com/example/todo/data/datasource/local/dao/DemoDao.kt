package com.example.todo.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.todo.data.models.entity.DemoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DemoDao {

    @Query("select * from DemoEntity")
    fun getAllEntities(): Flow<List<DemoEntity>>

    @Insert(onConflict = REPLACE)
    suspend fun addEntity(entity: DemoEntity)

    @Delete
    suspend fun deleteEntity(entity: DemoEntity)
}
