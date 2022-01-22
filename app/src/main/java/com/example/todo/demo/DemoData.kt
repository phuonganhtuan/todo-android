package com.example.todo.demo

import android.util.Log
import com.example.todo.data.models.entity.*
import com.example.todo.data.repository.TaskRepository
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

val bm1 = BookmarkEntity(
    type = BookmarkType.NUMBER.name,
    icon = "ic_ummark_num_1",
    markedIcon = "ic_ummark_num_1"
)

val bm2 = BookmarkEntity(
    type = BookmarkType.NUMBER.name,
    icon = "ic_ummark_num_2",
    markedIcon = "ic_ummark_num_2"
)

val bm3 = BookmarkEntity(
    type = BookmarkType.NUMBER.name,
    icon = "ic_ummark_num_3",
    markedIcon = "ic_ummark_num_3"
)

val bm4 = BookmarkEntity(
    type = BookmarkType.FLAG.name,
    icon = "ic_ummark_flag_1",
    markedIcon = "ic_ummark_flag_1"
)

val bm5 = BookmarkEntity(
    type = BookmarkType.FLAG.name,
    icon = "ic_ummark_flag_2",
    markedIcon = "ic_ummark_flag_2"
)

val taskDetail = TaskDetailEntity(
    note = "Create actionable plans for product",
    isReminder = false,
    reminderTime = 0,
    isRepeat = false,
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
    ),
    TaskEntity(
        title = "Happy birthday to me",
        categoryId = null,
        calendar = Calendar.getInstance().timeInMillis,
        isDone = false,
        isMarked = true,
        markId = null,
    ),
    TaskEntity(
        title = "Start making user flow for a new mobile application 2",
        categoryId = null,
        calendar = Calendar.getInstance().timeInMillis,
        isDone = false,
        isMarked = false,
        markId = null,
    ),
    TaskEntity(
        title = "Reseach product for DAP",
        categoryId = null,
        calendar = Calendar.getInstance().timeInMillis,
        isDone = true,
        isMarked = true,
        markId = null,
    ),
    TaskEntity(
        title = "Task 5 title demo",
        categoryId = null,
        calendar = Calendar.getInstance().timeInMillis,
        isDone = false,
        isMarked = true,
        markId = null,
    ),
    TaskEntity(
        title = "Task 6 title demo",
        categoryId = null,
        calendar = Calendar.getInstance().timeInMillis,
        isDone = true,
        isMarked = false,
        markId = null,
    ),
    TaskEntity(
        title = "Task 7 title demo",
        categoryId = null,
        calendar = Calendar.getInstance().timeInMillis,
        isDone = false,
        isMarked = false,
        markId = null,
    ),
    TaskEntity(
        title = "Task 8 title demo",
        categoryId = null,
        calendar = Calendar.getInstance().timeInMillis,
        isDone = false,
        isMarked = false,
        markId = null,
    ),
    TaskEntity(
        title = "Task 9 title demo",
        categoryId = null,
        calendar = Calendar.getInstance().timeInMillis,
        isDone = false,
        isMarked = false,
        markId = null,
    ),
    TaskEntity(
        title = "Task 10 title demo",
        categoryId = null,
        calendar = Calendar.getInstance().timeInMillis,
        isDone = true,
        isMarked = true,
        markId = null,
    ),
)

suspend fun createDemoData(repository: TaskRepository) {

    // Remove all existed data
    repository.deleteAttachments()
    repository.deleteTaskDetails()
    repository.deleteBookmarks()
    repository.deleteCategories()
    repository.deleteSubtasks()
    repository.deleteTasks()

    Log.d("demo_data", "removed old data")

    // Add categories
    val cat1Id = repository.addCategory(cat1)
    val cat2Id = repository.addCategory(cat2)
    val cat3Id = repository.addCategory(cat3)
    val cat4Id = repository.addCategory(cat4)

    Log.d("demo_data", "cat with id $cat1Id created!")
    Log.d("demo_data", "cat with id $cat2Id created!")
    Log.d("demo_data", "cat with id $cat3Id created!")
    Log.d("demo_data", "cat with id $cat4Id created!")

    // Add bookmarks
    val bm1Id = repository.addBookmark(bm1)
    val bm2Id = repository.addBookmark(bm2)
    val bm3Id = repository.addBookmark(bm3)
    val bm4Id = repository.addBookmark(bm4)
    val bm5Id = repository.addBookmark(bm5)

    Log.d("demo_data", "bookmark with id $bm1Id created!")
    Log.d("demo_data", "bookmark with id $bm2Id created!")
    Log.d("demo_data", "bookmark with id $bm3Id created!")
    Log.d("demo_data", "bookmark with id $bm4Id created!")
    Log.d("demo_data", "bookmark with id $bm5Id created!")

    val bmIds = listOf(bm1Id, bm2Id, bm3Id, bm4Id, bm5Id)
    val catIds = listOf(cat1Id, cat2Id, cat3Id, cat4Id)

    tasks.forEach {
        it.markId = bmIds.random().toInt()
        it.categoryId = catIds.random().toInt()

        // Add task
        val taskId = repository.addTask(it)
        taskDetail.taskId = taskId.toInt()

        // Add task detail
        repository.addTaskDetail(taskDetail)

        // Add attachments
        attachments.forEach { att ->
            att.taskId = taskId.toInt()
            repository.addAttachment(att)
        }

        // Add sub-tasks
        subTasks.forEach { sub ->
            sub.taskId = taskId.toInt()
            repository.addSubTasks(sub)
        }

        Log.d("demo_data", "task with title ${it.title} and id $taskId created!")
    }
}
