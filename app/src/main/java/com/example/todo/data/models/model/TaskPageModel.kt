package com.example.todo.data.models.model

import com.example.todo.data.models.entity.TaskEntity

data class TaskPageModel(
    override var id: Int,
    var type: TaskPageType,
    var tasks: List<TaskEntity>,
) : BaseModel()

enum class TaskPageType {
    TODAY, FUTURE, DONE
}
