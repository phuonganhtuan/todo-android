package com.trustedapp.todolist.planner.reminders.data.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class CountDownWidgetModel(
    @PrimaryKey(autoGenerate = true)
    override var id: Int = 0,
    var widgetId: Int = 0,
    var eventName: String = "",
    var date: Long = Calendar.getInstance().timeInMillis,
    var countType: String = CountDownType.REMAIN_DAYS.name,
    var iconIndex: Int = 0,
    var updateTime: Long = Calendar.getInstance().timeInMillis
) : BaseEntity()

enum class CountDownType {
    REMAIN_DAYS, DAYS
}
