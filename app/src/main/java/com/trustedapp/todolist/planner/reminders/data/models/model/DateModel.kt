package com.trustedapp.todolist.planner.reminders.data.models.model

import com.trustedapp.todolist.planner.reminders.data.models.entity.BaseEntity
import java.util.*

data class DateModel(
    override var id: Int,
    var date: Date,
    var isInMonth: Boolean,
    var hasTask: Boolean = false,
    var hasBirthday: Boolean = false,
): BaseEntity()
