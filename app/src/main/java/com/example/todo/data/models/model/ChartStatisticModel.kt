package com.example.todo.data.models.model

import com.example.todo.data.models.entity.BaseEntity

data class ChartStatisticModel(
    override var id: Int,
    var name: String,
    var value: String,
    var color: Int,
    var percent: Float,
): BaseEntity()
