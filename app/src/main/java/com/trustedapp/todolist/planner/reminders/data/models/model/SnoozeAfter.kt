package com.trustedapp.todolist.planner.reminders.data.models.model

data class SnoozeAfterModel(
    override var id: Int,
    var nameResId: Int,
    var offset: Long,
): BaseModel()