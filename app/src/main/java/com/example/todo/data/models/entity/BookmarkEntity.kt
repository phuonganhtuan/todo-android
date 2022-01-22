package com.example.todo.data.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    override var id: Int,
    var type: String = BookmarkType.FLAG.name,
    var icon: String,
    var markedIcon: String,
): BaseEntity()

enum class BookmarkType {
    NUMBER, FLAG
}
