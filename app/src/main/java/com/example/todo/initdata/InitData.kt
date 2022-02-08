package com.example.todo.initdata

import android.util.Log
import com.example.todo.data.models.entity.*
import com.example.todo.data.repository.TaskRepository
import com.example.todo.screens.newtask.ReminderTimeEnum
import com.example.todo.screens.newtask.ReminderTypeEnum
import com.example.todo.screens.newtask.RepeatAtEnum
import com.example.todo.utils.DateTimeUtils
import java.util.*

val cat1 = CategoryEntity(
    name = "Work",
    color = "#000000"
)

val cat2 = CategoryEntity(
    name = "Personal",
    color = "#000000"
)

val cat3 = CategoryEntity(
    name = "Birthday",
    color = "#000000"
)
val cat4 = CategoryEntity(
    name = "Wishlist",
    color = "#000000"
)

val bms = listOf(
    BookmarkEntity(
        type = BookmarkType.FLAG1.name,
        number = "",
        color = BookMarkColor.GREEN.name,
    ),
    BookmarkEntity(
        type = BookmarkType.FLAG1.name,
        number = "",
        color = BookMarkColor.BLACK.name,
    ),
    BookmarkEntity(
        type = BookmarkType.FLAG1.name,
        number = "",
        color = BookMarkColor.RED.name,
    ),
    BookmarkEntity(
        type = BookmarkType.FLAG1.name,
        number = "",
        color = BookMarkColor.ORANGE.name,
    ),
    BookmarkEntity(
        type = BookmarkType.FLAG1.name,
        number = "",
        color = BookMarkColor.BLUE.name,
    ),
    BookmarkEntity(
        type = BookmarkType.FLAG1.name,
        number = "",
        color = BookMarkColor.PURPLE.name,
    ),
    BookmarkEntity(
        type = BookmarkType.FLAG2.name,
        number = "",
        color = BookMarkColor.GREEN.name,
    ),
    BookmarkEntity(
        type = BookmarkType.FLAG2.name,
        number = "",
        color = BookMarkColor.BLACK.name,
    ),
    BookmarkEntity(
        type = BookmarkType.FLAG2.name,
        number = "",
        color = BookMarkColor.RED.name,
    ),
    BookmarkEntity(
        type = BookmarkType.FLAG2.name,
        number = "",
        color = BookMarkColor.ORANGE.name,
    ),
    BookmarkEntity(
        type = BookmarkType.FLAG2.name,
        number = "",
        color = BookMarkColor.BLUE.name,
    ),
    BookmarkEntity(
        type = BookmarkType.FLAG2.name,
        number = "",
        color = BookMarkColor.PURPLE.name,
    ),
    BookmarkEntity(
        type = BookmarkType.FLAG3.name,
        number = "",
        color = BookMarkColor.GREEN.name,
    ),
    BookmarkEntity(
        type = BookmarkType.FLAG3.name,
        number = "",
        color = BookMarkColor.BLACK.name,
    ),
    BookmarkEntity(
        type = BookmarkType.FLAG3.name,
        number = "",
        color = BookMarkColor.RED.name,
    ),
    BookmarkEntity(
        type = BookmarkType.FLAG3.name,
        number = "",
        color = BookMarkColor.ORANGE.name,
    ),
    BookmarkEntity(
        type = BookmarkType.FLAG3.name,
        number = "",
        color = BookMarkColor.BLUE.name,
    ),
    BookmarkEntity(
        type = BookmarkType.FLAG3.name,
        number = "",
        color = BookMarkColor.PURPLE.name,
    ),
    BookmarkEntity(
        type = BookmarkType.NUMBER.name,
        number = "1",
        color = BookMarkColor.GREEN.name,
    ),
    BookmarkEntity(
        type = BookmarkType.NUMBER.name,
        number = "2",
        color = BookMarkColor.GREEN.name,
    ),
    BookmarkEntity(
        type = BookmarkType.NUMBER.name,
        number = "3",
        color = BookMarkColor.GREEN.name,
    ),
    BookmarkEntity(
        type = BookmarkType.NUMBER.name,
        number = "4",
        color = BookMarkColor.GREEN.name,
    ),
    BookmarkEntity(
        type = BookmarkType.NUMBER.name,
        number = "5",
        color = BookMarkColor.GREEN.name,
    ),
)

val taskDetail = TaskDetailEntity(
    note = "Create actionable plans for product",
    isReminder = true,
    isRepeat = true,
    taskId = 0,
)

val attImage = AttachmentEntity(
    name = "73-937529",
    extension = ".jpg",
    type = AttachmentType.IMAGE.name,
    taskId = 0,
    path = "",
)

val attAudio = AttachmentEntity(
    name = "demo-audio-file-attachment",
    extension = ".m4a",
    type = AttachmentType.AUDIO.name,
    taskId = 0,
    path = "",
)

val attVideo = AttachmentEntity(
    name = "videodemo_783683djfdsfllSDBJ",
    extension = ".mp4",
    type = AttachmentType.VIDEO.name,
    taskId = 0,
    path = "",
)

val subt1 = SubTaskEntity(
    name = "Subtask 1",
    isDone = false,
    taskId = 0,
)

val subt2 = SubTaskEntity(
    name = "Subtask 2",
    isDone = true,
    taskId = 0,
)

val subt3 = SubTaskEntity(
    name = "Subtask 3",
    isDone = false,
    taskId = 0,
)

val subTasks = listOf(subt1, subt2, subt3)
val attachments = listOf(attAudio, attImage, attVideo)

val tasks = listOf<TaskEntity>(
    TaskEntity(
        title = "Start making user flow for a new mobile application",
        categoryId = null,
        calendar = Calendar.getInstance().timeInMillis,
        isDone = true,
        isMarked = false,
        markId = null,
        dueDate = DateTimeUtils.getComparableDateString(Calendar.getInstance().time),
    ),
    TaskEntity(
        title = "Happy birthday to me",
        categoryId = null,
        calendar = Calendar.getInstance().timeInMillis,
        isDone = false,
        isMarked = true,
        markId = null,
        dueDate = DateTimeUtils.getComparableDateString(Calendar.getInstance().time),
    ),
    TaskEntity(
        title = "Start making user flow for a new mobile application 2",
        categoryId = null,
        calendar = Calendar.getInstance().timeInMillis,
        isDone = false,
        isMarked = false,
        markId = null,
        dueDate = DateTimeUtils.getComparableDateString(Calendar.getInstance().time),
    ),
    TaskEntity(
        title = "Reseach product for DAP",
        categoryId = null,
        calendar = Calendar.getInstance().timeInMillis,
        isDone = true,
        isMarked = true,
        markId = null,
        dueDate = DateTimeUtils.getComparableDateString(Calendar.getInstance().time),
    ),
    TaskEntity(
        title = "Task 5 title demo",
        categoryId = null,
        calendar = Calendar.getInstance().timeInMillis,
        isDone = false,
        isMarked = true,
        markId = null,
        dueDate = DateTimeUtils.getComparableDateString(Calendar.getInstance().time),
    ),
    TaskEntity(
        title = "Task 6 title demo",
        categoryId = null,
        calendar = Calendar.getInstance().timeInMillis,
        isDone = true,
        isMarked = false,
        markId = null,
        dueDate = DateTimeUtils.getComparableDateString(Calendar.getInstance().time),
    ),
    TaskEntity(
        title = "Task 7 title demo",
        categoryId = null,
        calendar = Calendar.getInstance().timeInMillis,
        isDone = false,
        isMarked = false,
        markId = null,
        dueDate = DateTimeUtils.getComparableDateString(Calendar.getInstance().time),
    ),
    TaskEntity(
        title = "Task 8 title demo",
        categoryId = null,
        calendar = Calendar.getInstance().timeInMillis,
        isDone = false,
        isMarked = false,
        markId = null,
        dueDate = DateTimeUtils.getComparableDateString(Calendar.getInstance().time),
    ),
    TaskEntity(
        title = "Task 9 title demo",
        categoryId = null,
        calendar = Calendar.getInstance().timeInMillis,
        isDone = false,
        isMarked = false,
        markId = null,
        dueDate = DateTimeUtils.getComparableDateString(Calendar.getInstance().time),
    ),
    TaskEntity(
        title = "Task 10 title demo",
        categoryId = null,
        calendar = Calendar.getInstance().timeInMillis,
        isDone = true,
        isMarked = true,
        markId = null,
        dueDate = DateTimeUtils.getComparableDateString(Calendar.getInstance().time),
    ),
)

suspend fun createInitData(repository: TaskRepository) {

    // Remove all existed data
//    repository.deleteAttachments()
//    repository.deleteTaskDetails()
//    repository.deleteBookmarks()
//    repository.deleteCategories()
//    repository.deleteSubtasks()
//    repository.deleteTasks()

    // Add categories
    val cat1Id = repository.addCategory(cat1)
    val cat2Id = repository.addCategory(cat2)
    val cat3Id = repository.addCategory(cat3)
    val cat4Id = repository.addCategory(cat4)

    // Add bookmarks
    val bookmarkIds = mutableListOf<Long>()
    bms.forEach {
        val id = repository.addBookmark(it)
        bookmarkIds.add(id)
    }

//    val catIds = listOf(cat1Id, cat2Id, cat3Id, cat4Id)

//    tasks.forEach {
//        it.markId = bookmarkIds.random().toInt()
//        it.categoryId = catIds.random().toInt()
//
//        // Add task
//        val taskId = repository.addTask(it)
//        taskDetail.taskId = taskId.toInt()
//
//        // Add task detail
//        repository.addTaskDetail(taskDetail)
//
//        val reminder = ReminderEntity(
//            reminderType = ReminderTypeEnum.NOTIFICATION.name,
//            reminderTime = ReminderTimeEnum.THIRTY_MINUTES_BEFORE.name,
//            screenLockReminder = false,
//            enableRepeat = true,
//            repeatTime = RepeatAtEnum.WEEKLY.name,
//            taskId = taskId.toInt(),
//            time = it.calendar ?: 0L
//        )
//
//        repository.addReminder(reminder)
//
//        // Add attachments
//        attachments.forEach { att ->
//            att.taskId = taskId.toInt()
//            repository.addAttachment(att)
//        }
//
//        // Add sub-tasks
//        subTasks.forEach { sub ->
//            sub.taskId = taskId.toInt()
//            repository.addSubTasks(sub)
//        }
//
//        Log.d("demo_data", "task with title ${it.title} and id $taskId created!")
//    }
}
