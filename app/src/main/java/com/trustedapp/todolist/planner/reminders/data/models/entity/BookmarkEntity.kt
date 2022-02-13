package com.trustedapp.todolist.planner.reminders.data.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    override var id: Int = 0,
    var type: String = BookmarkType.NUMBER.name,
    var number: String = "",
    var color: String = BookMarkColor.GREEN.name,
): BaseEntity()

enum class BookmarkType {
    NUMBER, FLAG1, FLAG2, FLAG3
}

enum class BookMarkColor {
    GREEN, BLACK, RED, ORANGE, BLUE, PURPLE
}
