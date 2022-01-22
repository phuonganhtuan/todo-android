package com.example.todo.data.datasource.remote.datasource

import com.example.todo.data.models.entity.DemoEntity

interface MainRemoteDataSource {

    suspend fun getRandomActivity(): DemoEntity
}
