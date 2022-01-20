package com.example.todo.data.datasource.remote.datasource

import com.example.todo.data.models.DemoEntity

interface MainRemoteDataSource {

    suspend fun getRandomActivity(): DemoEntity
}
