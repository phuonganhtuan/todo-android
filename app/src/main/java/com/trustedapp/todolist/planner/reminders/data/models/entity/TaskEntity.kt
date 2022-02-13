package com.trustedapp.todolist.planner.reminders.data.models.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

data class Task(
    @Embedded var task: TaskEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    var detail: TaskDetailEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    var subTasks: List<SubTaskEntity> = emptyList(),
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    var attachments: List<AttachmentEntity> = emptyList(),
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    var category: CategoryEntity?,
    @Relation(
        parentColumn = "markId",
        entityColumn = "id"
    )
    var bookmark: BookmarkEntity?,

    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    var reminder: ReminderEntity? = null,
)

data class TaskShort(
    @Embedded var task: TaskEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    var category: CategoryEntity?,
    @Relation(
        parentColumn = "markId",
        entityColumn = "id"
    )
    var bookmark: BookmarkEntity?,
)

@Entity
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    override var id: Int = 0,
    var title: String = "",
    var categoryId: Int?,
    var calendar: Long?,
    var dueDate: String?,
    var isDone: Boolean = false,
    var isMarked: Boolean = false,
    var markId: Int?,
) : BaseEntity()

@Entity
data class TaskDetailEntity(
    @PrimaryKey(autoGenerate = true)
    override var id: Int = 0,
    var taskId: Int,
    var note: String = "",
    var isReminder: Boolean = false,
    var isRepeat: Boolean = false,
) : BaseEntity()
