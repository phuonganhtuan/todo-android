package com.example.todo.data.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todo.screens.newtask.ReminderTimeEnum
import com.example.todo.screens.newtask.ReminderTypeEnum
import com.example.todo.screens.newtask.RepeatAtEnum

@Entity
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    override var id: Int = 0,
    var reminderType: String = ReminderTypeEnum.NOTIFICATION.name,
    var reminderTime: String = ReminderTimeEnum.NONE.name,
    var screenLockReminder: Boolean = false,
    var enableRepeat: Boolean = false,
    var repeatTime: String = RepeatAtEnum.NONE.name,
    var time: Long,
    var taskId: Int,
): BaseEntity()
