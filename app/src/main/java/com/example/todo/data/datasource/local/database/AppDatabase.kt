package com.example.todo.data.datasource.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todo.data.datasource.local.dao.TaskDao
import com.example.todo.data.models.entity.*

@Database(
    entities = [
        TaskEntity::class,
        TaskDetailEntity::class,
        AttachmentEntity::class,
        CategoryEntity::class,
        BookmarkEntity::class,
        SubTaskEntity::class,
        ReminderEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {

        private const val DATABASE_NAME = "TodoDB"

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java, DATABASE_NAME
        ).build()
    }
}
