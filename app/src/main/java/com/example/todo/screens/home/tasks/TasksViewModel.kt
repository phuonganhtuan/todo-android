package com.example.todo.screens.home.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.models.entity.AttachmentEntity
import com.example.todo.data.models.entity.CategoryEntity
import com.example.todo.data.models.entity.SubTaskEntity
import com.example.todo.data.models.entity.TaskEntity
import com.example.todo.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(private val repo: TaskRepository) : ViewModel() {

    fun addDemoData() = viewModelScope.launch {
        val taskEntity = TaskEntity(
            title = "Task title",
            categoryId = null,
            calendar = Calendar.getInstance().timeInMillis,
            isDone = false,
            isMarked = false,
            markId = null,
        )
        val category = CategoryEntity(
            name = "BIRTHDAY",
            color = "#000000"
        )
        val catId = repo.addCategory(category)
        taskEntity.categoryId = catId.toInt()
        val taskId = repo.addTask(taskEntity)

        repeat(listOf(1, 2, 3, 4, 5).size) {
            val attachmentEntity = AttachmentEntity(
                taskId = taskId.toInt(),
            )
            repo.addAttachment(attachmentEntity)
        }

        repeat(listOf(1, 2, 3).size) {
            val subTaskEntity = SubTaskEntity(
                taskId = taskId.toInt(),
                name = "Sub task"
            )
            repo.addSubTasks(subTaskEntity)
        }
    }
}
