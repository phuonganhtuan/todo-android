package com.example.todo.data.models.entity

import android.graphics.Bitmap
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
    var size: String = "",
    var takenDate: String? = "",
) : BaseEntity()

enum class AttachmentType {
    AUDIO, VIDEO, IMAGE, ALBUM
}

data class AttachmentAlbumEntity(
    override var id: Int

): BaseEntity()
