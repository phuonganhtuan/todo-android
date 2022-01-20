package com.example.todo.data.repository.impl

import com.example.todo.data.datasource.local.datasource.MainLocalDataSource
import com.example.todo.data.datasource.remote.datasource.MainRemoteDataSource
import com.example.todo.data.models.DemoEntity
import com.example.todo.data.repository.MainRepository
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val localDataSource: MainLocalDataSource,
    private val remoteDataSource: MainRemoteDataSource
) : MainRepository {

    override fun getAllEntities() = localDataSource.getAllEntities()

    override suspend fun addEntity(entity: DemoEntity) = localDataSource.addEntity(entity)

    override suspend fun deleteEntity(entity: DemoEntity) = localDataSource.deleteEntity(entity)

    override suspend fun getRandomActivity() = remoteDataSource.getRandomActivity()
}
