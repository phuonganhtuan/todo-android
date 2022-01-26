package com.example.todo.data.models.model

import com.example.todo.data.models.entity.BaseEntity
import java.util.*

data class DateModel(
    override var id: Int,
    var date: Date,
    var isInMonth: Boolean,
): BaseEntity()
