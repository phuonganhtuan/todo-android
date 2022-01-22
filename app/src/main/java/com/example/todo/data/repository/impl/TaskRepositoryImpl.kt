package com.example.todo.data.repository.impl

import com.example.todo.data.datasource.local.datasource.TaskLocalDataSource
import com.example.todo.data.models.entity.*
import com.example.todo.data.repository.TaskRepository
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDataSource: TaskLocalDataSource,
) : TaskRepository {

    override fun getShortTasks() = taskDataSource.getShortTasks()
    override fun getTask(id: Int) = taskDataSource.getTask(id)
    override suspend fun addTask(entity: TaskEntity) = taskDataSource.addTask(entity)
    override suspend fun addTaskDetail(entity: TaskDetailEntity) = taskDataSource.addTaskDetail(entity)
    override suspend fun addSubTasks(entity: SubTaskEntity) = taskDataSource.addSubTasks(entity)
    override suspend fun addAttachment(entity: AttachmentEntity) = taskDataSource.addAttachment(entity)
    override suspend fun addBookmark(entity: BookmarkEntity) = taskDataSource.addBookmark(entity)
    override suspend fun addCategory(entity: CategoryEntity) = taskDataSource.addCategory(entity)
}
