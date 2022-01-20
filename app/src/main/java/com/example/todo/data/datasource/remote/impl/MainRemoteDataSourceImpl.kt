package com.example.todo.data.datasource.remote.impl

import com.example.todo.data.datasource.remote.api.ApiService
import com.example.todo.data.datasource.remote.datasource.MainRemoteDataSource
import javax.inject.Inject

class MainRemoteDataSourceImpl @Inject constructor(private val api: ApiService) :
    MainRemoteDataSource {

    override suspend fun getRandomActivity() = api.getRandomActivity()
}
