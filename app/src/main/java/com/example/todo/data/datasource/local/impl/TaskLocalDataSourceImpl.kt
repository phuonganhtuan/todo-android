package com.example.todo.data.datasource.local.impl

import com.example.todo.data.datasource.local.dao.TaskDao
import com.example.todo.data.datasource.local.datasource.TaskLocalDataSource
import com.example.todo.data.models.entity.*
import javax.inject.Inject

class TaskLocalDataSourceImpl @Inject constructor(private val dao: TaskDao) : TaskLocalDataSource {

    override fun getShortTasks() = dao.getShortTasks()
    override fun getTask(id: Int) = dao.getTask(id)
    override suspend fun addTask(entity: TaskEntity) = dao.addTask(entity)
    override suspend fun addTaskDetail(entity: TaskDetailEntity) = dao.addTaskDetail(entity)
    override suspend fun addSubTasks(entity: SubTaskEntity) = dao.addSubTasks(entity)
    override suspend fun addAttachment(entity: AttachmentEntity) = dao.addAttachment(entity)
    override suspend fun addBookmark(entity: BookmarkEntity) = dao.addBookmark(entity)
    override suspend fun addCategory(entity: CategoryEntity) = dao.addCategory(entity)
    override suspend fun addReminder(entity: ReminderEntity) = dao.addReminder(entity)

    override suspend fun updateTask(entity: TaskEntity) = dao.updateTask(entity)
    override suspend fun updateCategory(entity: CategoryEntity) = dao.updateCategory(entity)
    override suspend fun updateTaskDetail(entity: TaskDetailEntity) = dao.updateTaskDetail(entity)
    override suspend fun updateSubTask(entity: SubTaskEntity) = dao.updateSubTask(entity)
    override suspend fun updateAttachment(entity: AttachmentEntity) = dao.updateAttachment(entity)
    override suspend fun updateReminder(entity: ReminderEntity) = dao.updateReminder(entity)

    override suspend fun deleteCategories() = dao.deleteCategories()
    override suspend fun deleteTasks() = dao.deleteTasks()
    override suspend fun deleteTaskDetails() = dao.deleteTaskDetails()
    override suspend fun deleteBookmarks() = dao.deleteBookmarks()
    override suspend fun deleteAttachments() = dao.deleteAttachments()
    override suspend fun deleteSubtasks() = dao.deleteSubtasks()

    override fun getCategories() = dao.getCategories()

    override fun searchTaskByName(name: String) = dao.searchTaskByName(name)
    override fun getTaskInDay(dayString: String) = dao.getTaskInDay(dayString)

    override suspend fun deleteAttachment(id: Int) = dao.deleteAttachment(id)
    override suspend fun deleteSubtask(id: Int) = dao.deleteSubtask(id)
    override suspend fun deleteTaskDetail(id: Int) = dao.deleteTaskDetail(id)
    override suspend fun deleteTask(id: Int) = dao.deleteTask(id)

    override fun getBookmarks() = dao.getBookmarks()
    override fun getReminder(taskId: Int) = dao.getReminder(taskId)
    override suspend fun deleteReminder(taskId: Int) = dao.deleteReminder(taskId)
}
