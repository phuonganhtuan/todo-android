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
    override suspend fun addTaskDetail(entity: TaskDetailEntity) =
        taskDataSource.addTaskDetail(entity)

    override suspend fun addSubTasks(entity: SubTaskEntity) = taskDataSource.addSubTasks(entity)
    override suspend fun addAttachment(entity: AttachmentEntity) =
        taskDataSource.addAttachment(entity)

    override suspend fun addBookmark(entity: BookmarkEntity) = taskDataSource.addBookmark(entity)
    override suspend fun addCategory(entity: CategoryEntity) = taskDataSource.addCategory(entity)

    override suspend fun updateTask(entity: TaskEntity) = taskDataSource.updateTask(entity)
    override suspend fun updateCategory(entity: CategoryEntity) =
        taskDataSource.updateCategory(entity)

    override suspend fun updateTaskDetail(entity: TaskDetailEntity) =
        taskDataSource.updateTaskDetail(entity)

    override suspend fun updateSubTask(entity: SubTaskEntity) = taskDataSource.updateSubTask(entity)
    override suspend fun updateAttachment(entity: AttachmentEntity) =
        taskDataSource.updateAttachment(entity)

    override suspend fun deleteCategories() = taskDataSource.deleteCategories()
    override suspend fun deleteTasks() = taskDataSource.deleteTasks()
    override suspend fun deleteTaskDetails() = taskDataSource.deleteTaskDetails()
    override suspend fun deleteBookmarks() = taskDataSource.deleteBookmarks()
    override suspend fun deleteAttachments() = taskDataSource.deleteAttachments()
    override suspend fun deleteSubtasks() = taskDataSource.deleteSubtasks()

    override fun getCategories() = taskDataSource.getCategories()

    override fun searchTaskByName(name: String) = taskDataSource.searchTaskByName(name)
    override fun getTaskInDay(dayString: String) = taskDataSource.getTaskInDay(dayString)

    override suspend fun deleteAttachment(id: Int) = taskDataSource.deleteAttachment(id)
    override suspend fun deleteSubtask(id: Int) = taskDataSource.deleteSubtask(id)
    override suspend fun deleteTaskDetail(id: Int) = taskDataSource.deleteTaskDetail(id)
    override suspend fun deleteTask(id: Int) = taskDataSource.deleteTask(id)
}
