package com.example.todo.data.datasource.local.datasource

import com.example.todo.data.models.entity.DemoEntity
import kotlinx.coroutines.flow.Flow

interface MainLocalDataSource {

    fun getAllEntities(): Flow<List<DemoEntity>>

    suspend fun addEntity(entity: DemoEntity)

    suspend fun deleteEntity(entity: DemoEntity)
}
