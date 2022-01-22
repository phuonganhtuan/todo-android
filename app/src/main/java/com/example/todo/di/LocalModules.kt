package com.example.todo.di

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.todo.data.datasource.local.datasource.MainLocalDataSource
import com.example.todo.data.datasource.local.impl.MainLocalDataSourceImpl
import com.example.todo.data.datasource.local.database.AppDatabase
import com.example.todo.data.datasource.remote.datasource.MainRemoteDataSource
import com.example.todo.data.datasource.remote.impl.MainRemoteDataSourceImpl
import com.example.todo.data.repository.MainRepository
import com.example.todo.data.repository.impl.MainRepositoryImpl
import com.example.todo.screens.home.tasks.TasksPagerAdapter
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class LocalModules {

    @Binds
    abstract fun bindMainRepo(
        mainRepoImpl: MainRepositoryImpl
    ): MainRepository

    @Binds
    abstract fun bindMainLocalDataSource(
        mainLocalDataSource: MainLocalDataSourceImpl
    ): MainLocalDataSource

    @Binds
    abstract fun bindMainRemoteDataSource(
        mainRemoteDataSource: MainRemoteDataSourceImpl
    ): MainRemoteDataSource
}

@Module
@InstallIn(ViewModelComponent::class)
object DBModules {

    @Provides
    fun provideDemoDatabase(
        @ApplicationContext app: Context
    ) = AppDatabase.invoke(app)

    @Provides
    fun provideYourDao(db: AppDatabase) = db.demoDao()
}

@Module
@InstallIn(ActivityComponent::class)
object ActivityModules {

    @Provides
    @ActivityScoped
    fun provideAdapterFragmentState(@ActivityContext context: Context): TasksPagerAdapter {
        return TasksPagerAdapter(context as FragmentActivity)
    }
}
