package com.example.todo.data.models.entity

data class TaskEntity(
    override var id: Int = 0,
    var name: String = "",
): BaseEntity()
