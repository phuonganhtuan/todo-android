package com.example.todo.data.datasource.remote.api

import com.example.todo.data.models.DemoEntity
import retrofit2.http.GET

interface ApiService {

    @GET("activity")
    suspend fun getRandomActivity(): DemoEntity
}
