package com.example.todo.data.models.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.*

data class Task(
    @Embedded var task: TaskEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    var category: CategoryEntity,
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
)

data class TaskShort(
    @Embedded var task: TaskEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    var category: CategoryEntity,
)

@Entity
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    override var id: Int,
    var title: String = "",
    var categoryId: Int?,
    var calendar: Date?,
    var isDone: Boolean = false,
    var isMarked: Boolean = false,
    var markId: Int?,
): BaseEntity()

@Entity
data class TaskDetailEntity(
    @PrimaryKey(autoGenerate = true)
    override var id: Int,
    var taskId: Int,
    var note: String = "",
    var isReminder: Boolean = false,
    var reminderTime: Long,
    var isRepeat: Boolean = false,
): BaseEntity()
