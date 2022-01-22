package com.example.todo.data.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AttachmentEntity(
    @PrimaryKey(autoGenerate = true)
    override var id: Int = 0,
    var name: String = "",
    var extension: String = "",
    var path: String = "",
    var taskId: Int,
    var type: String = AttachmentType.IMAGE.name,
): BaseEntity()

enum class AttachmentType {
    AUDIO, VIDEO, IMAGE
}
