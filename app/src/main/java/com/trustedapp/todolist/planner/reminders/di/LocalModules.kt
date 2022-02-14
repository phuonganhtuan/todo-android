package com.trustedapp.todolist.planner.reminders.di

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.trustedapp.todolist.planner.reminders.data.datasource.local.database.AppDatabase
import com.trustedapp.todolist.planner.reminders.data.datasource.local.datasource.TaskLocalDataSource
import com.trustedapp.todolist.planner.reminders.data.datasource.local.impl.TaskLocalDataSourceImpl
import com.trustedapp.todolist.planner.reminders.data.repository.TaskRepository
import com.trustedapp.todolist.planner.reminders.data.repository.impl.TaskRepositoryImpl
import com.trustedapp.todolist.planner.reminders.screens.home.tasks.TasksPagerAdapter
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
        mainRepoImpl: TaskRepositoryImpl
    ): TaskRepository

    @Binds
    abstract fun bindMainLocalDataSource(
        mainLocalDataSource: TaskLocalDataSourceImpl
    ): TaskLocalDataSource
}

@Module
@InstallIn(ViewModelComponent::class)
object DBModules {

    @Provides
    fun provideDemoDatabase(
        @ApplicationContext app: Context
    ) = AppDatabase.invoke(app)

    @Provides
    fun provideYourDao(db: AppDatabase) = db.taskDao()
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