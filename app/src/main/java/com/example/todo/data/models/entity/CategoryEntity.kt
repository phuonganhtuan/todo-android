package com.example.todo.data.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    override var id: Int = 0,
    var name: String,
    var color: String = "",
): BaseEntity()
