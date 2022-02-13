package com.trustedapp.todolist.planner.reminders.data.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SubTaskEntity(
    @PrimaryKey(autoGenerate = true)
    override var id: Int = 0,
    var name: String,
    var taskId: Int,
    var isDone: Boolean = false,
): BaseEntity()
