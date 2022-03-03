package com.trustedapp.todolist.planner.reminders.data.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.trustedapp.todolist.planner.reminders.widget.widgetBgColors

@Entity
data class StandardWidgetModel(
    @PrimaryKey(autoGenerate = true)
    override var id: Int = 0,
    var widgetId: Int = 0,
    var color: Int = widgetBgColors[0],
    var alpha: Int = 100,
    var isDark: Boolean = false,
    var isOnlyToday: Boolean = false,
    var categoryId: Int? = null,
    var containCompleted: Boolean = true,
) : BaseEntity()

enum class FilterTime { ALL_TIME, TODAY }
