package com.example.todo.data.datasource.remote.retrofit

import com.example.todo.data.datasource.remote.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)

    private const val BASE_URL = "https://www.boredapi.com/api/"

    private fun getRetrofit() =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}
