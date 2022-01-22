package com.example.todo.data.repository

import com.example.todo.data.models.entity.DemoEntity
import kotlinx.coroutines.flow.Flow

interface MainRepository {

    fun getAllEntities(): Flow<List<DemoEntity>>

    suspend fun addEntity(entity: DemoEntity)

    suspend fun deleteEntity(entity: DemoEntity)

    suspend fun getRandomActivity(): DemoEntity
}
