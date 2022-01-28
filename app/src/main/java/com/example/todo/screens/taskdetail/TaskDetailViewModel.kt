package com.example.todo.screens.taskdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.models.entity.CategoryEntity
import com.example.todo.data.models.entity.Task
import com.example.todo.data.repository.TaskRepository
import com.example.todo.demo.bm5
import com.example.todo.demo.cat1
import com.example.todo.demo.taskDetail
import com.example.todo.demo.tasks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(private val repository: TaskRepository) : ViewModel() {

    private var taskId = 0

    val task: StateFlow<Task> get() = _task
    private val _task = MutableStateFlow(
        Task(
            task = tasks[0],
            category = cat1,
            detail = taskDetail,
            bookmark = bm5
        )
    )

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

    fun initData(taskId: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            this@TaskDetailViewModel.taskId = taskId
            _task.value = repository.getTask(taskId)
        }
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
}
