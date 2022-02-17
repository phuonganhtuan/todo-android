package com.trustedapp.todolist.planner.reminders.data.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.trustedapp.todolist.planner.reminders.screens.newtask.ReminderTimeEnum
import com.trustedapp.todolist.planner.reminders.screens.newtask.ReminderTypeEnum
import com.trustedapp.todolist.planner.reminders.screens.newtask.RepeatAtEnum

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

@Entity
data class ReminderTimeEntity(
    @PrimaryKey(autoGenerate = true)
    override var id: Int = 0,
    var name: String = ReminderTimeEnum.CUSTOM_DAY_BEFORE.name, // ReminderTimeEnum.name
    var offset: Long
): BaseEntity()
