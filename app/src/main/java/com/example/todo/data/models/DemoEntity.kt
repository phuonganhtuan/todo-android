package com.example.todo.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DemoEntity(

    @PrimaryKey(autoGenerate = true)
    override var id: Int = 0,
    val accessibility: Double?,
    val activity: String?,
    val key: String?,
    val link: String?,
    val participants: Int?,
    val price: Double?,
    val type: String?,
): BaseEntity()
