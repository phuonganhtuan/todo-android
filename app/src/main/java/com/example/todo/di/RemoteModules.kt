package com.example.todo.di

import com.example.todo.data.datasource.remote.api.ApiService
import com.example.todo.data.datasource.remote.retrofit.RetrofitBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
object RemoteModules {

    @Provides
    fun provideApiService(
    ): ApiService {
        return RetrofitBuilder.apiService
    }
}
