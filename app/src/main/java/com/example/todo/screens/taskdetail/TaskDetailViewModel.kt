package com.example.todo.screens.taskdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.models.entity.*
import com.example.todo.data.repository.TaskRepository
import com.example.todo.demo.bms
import com.example.todo.demo.cat1
import com.example.todo.demo.taskDetail
import com.example.todo.demo.tasks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class TaskDetailViewModel @Inject constructor(private val repository: TaskRepository) :
    ViewModel() {

    private var taskId = 0

    val isRemoved: StateFlow<Boolean> get() = _isRemoved
    private val _isRemoved = MutableStateFlow(false)

    val task: StateFlow<Task> get() = _task
    private val _task = MutableStateFlow(
        Task(
            task = tasks[0],
            category = cat1,
            detail = taskDetail,
            bookmark = bms[0]
        )
    )

    var selectedBookmark: BookmarkEntity? = null

    val bookmarks: StateFlow<List<BookmarkEntity>>
        get() = _bookmarks

    private val _bookmarks = repository.getBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val attachments: StateFlow<List<AttachmentEntity>> get() = _attachments
    private val _attachments = MutableStateFlow(emptyList<AttachmentEntity>())

    val subtasks: StateFlow<List<SubTaskEntity>> get() = _subtasks
    private val _subtasks = MutableStateFlow(emptyList<SubTaskEntity>())

    val selectedCatIndex: StateFlow<Int> get() = _selectedCatIndex
    private val _selectedCatIndex = MutableStateFlow(-1)

    val categories: StateFlow<List<CategoryEntity>> get() = _categories
    private val _categories = repository
        .getCategories()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(2000),
            emptyList()
        )

    val isEditing: StateFlow<Boolean> get() = _isEditing
    private val _isEditing = MutableStateFlow(false)

    fun toEditMode() {
        _isEditing.value = true
    }

    fun toViewMode() {
        _isEditing.value = false
    }

    fun markAsDone() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val task = _task.value.task.copy()
            task.isDone = !task.isDone
            repository.updateTask(task)
            _task.value = repository.getTask(taskId)
        }
    }

    fun initData(taskId: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            this@TaskDetailViewModel.taskId = taskId
            _task.value = repository.getTask(taskId)
        }
        withContext(Dispatchers.IO) {
            _attachments.value = _task.value.attachments
        }
        withContext(Dispatchers.IO) {
            _subtasks.value = _task.value.subTasks
        }
    }

    fun removeAttachment(index: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            try {
                val atts = _attachments.value.toMutableList()
                atts.removeAt(index)
                _attachments.value = atts
            } catch (exception: Exception) {
            }
        }
    }

    fun addAttachment(atts: List<AttachmentEntity>) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val currentAtts = _attachments.value.toMutableList()
            currentAtts.addAll(atts)
            _attachments.value = currentAtts
        }
    }

    fun addSubTask() = viewModelScope.launch {
        val subTasks = _subtasks.value.toMutableList()
        subTasks.add(
            SubTaskEntity(
                name = "",
                isDone = false,
                taskId = 0,
                id = Random.nextInt()
            )
        )
        _subtasks.value = subTasks
    }

    fun setSubTasks(subTask: List<SubTaskEntity>) = viewModelScope.launch {
        _subtasks.value = subTask
    }

    fun updateSubTaskTitle(index: Int, title: String) = viewModelScope.launch {
        _subtasks.value[index].name = title
    }

    fun updateSubTaskState(index: Int, state: Boolean) = viewModelScope.launch {
        _subtasks.value[index].isDone = state
    }

    fun selectCat(index: Int) {
        _selectedCatIndex.value = index
    }

    fun createCategory(name: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val entity = CategoryEntity(name = name)
            repository.addCategory(entity)
        }
    }

    fun duplicateTask() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val newTask = _task.value.copy()
            val newTaskEntity = TaskEntity(
                title = newTask.task.title,
                categoryId = newTask.task.categoryId,
                calendar = newTask.task.calendar,
                dueDate = newTask.task.dueDate,
                isDone = newTask.task.isDone,
                isMarked = newTask.task.isMarked,
                markId = newTask.task.markId,
            )
            val taskId = repository.addTask(newTaskEntity)
            val newDetail = TaskDetailEntity(
                taskId = taskId.toInt(),
                note = newTask.detail.note,
                isReminder = newTask.detail.isReminder,
                isRepeat = newTask.detail.isRepeat,
                reminderTime = newTask.detail.reminderTime,
            )
            repository.addTaskDetail(newDetail)
            newTask.attachments.forEach { att ->
                val newAtt = AttachmentEntity(
                    name = att.name,
                    extension = att.extension,
                    path = att.path,
                    taskId = taskId.toInt(),
                    type = att.type,
                )
                repository.addAttachment(newAtt)
            }
            newTask.subTasks.forEach { sub ->
                val newSubTask = SubTaskEntity(
                    name = sub.name,
                    taskId = taskId.toInt(),
                    isDone = sub.isDone,
                )
                repository.addSubTasks(newSubTask)
            }
        }
    }

    fun deleteTask() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            task.value.apply {
                attachments.forEach {
                    repository.deleteAttachment(it.id)
                }
                subTasks.forEach {
                    repository.deleteSubtask(it.id)
                }
                repository.deleteTaskDetail(taskDetail.id)
                repository.deleteTask(task.id)
                _isRemoved.value = true
            }
        }
    }

    fun updateBookmark() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _task.value.task.markId = selectedBookmark?.id
            _task.value.task.isMarked = true
            _task.value.bookmark = selectedBookmark
            repository.updateTask(_task.value.task)
        }
    }
}
