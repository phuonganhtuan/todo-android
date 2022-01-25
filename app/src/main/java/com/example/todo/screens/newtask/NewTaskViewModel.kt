package com.example.todo.screens.newtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.models.entity.CategoryEntity
import com.example.todo.data.models.entity.SubTaskEntity
import com.example.todo.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class NewTaskViewModel @Inject constructor(private val repository: TaskRepository) : ViewModel() {

    val subTasks: StateFlow<List<SubTaskEntity>> get() = _subTasks
    private val _subTasks = MutableStateFlow<MutableList<SubTaskEntity>>(mutableListOf())

    val canAddSubTask: StateFlow<Boolean> get() = _canAddSubTask
    private val _canAddSubTask = MutableStateFlow(false)

    val categories: StateFlow<List<CategoryEntity>> get() = _categories
    private val _categories = repository
        .getCategories()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(2000),
            emptyList()
        )

    val selectedCatIndex: StateFlow<Int> get() = _selectedCatIndex
    private val _selectedCatIndex = MutableStateFlow(-1)

    init {
        addSubTask()
    }

    fun addSubTask() {
        val currentSubTasks = _subTasks.value.toMutableList()
        currentSubTasks.add(
            SubTaskEntity(
                name = "",
                isDone = false,
                taskId = 0,
                id = Random.nextInt()
            )
        )
        _subTasks.value = currentSubTasks
    }

    fun selectCat(index: Int) {
        _selectedCatIndex.value = index
    }

    fun updateSubTask(index: Int, title: String) = viewModelScope.launch {
        _subTasks.value[index].name = title
        _canAddSubTask.value = !(_subTasks.value.size == 1 && _subTasks.value[0].name.isEmpty())
    }

    fun removeSubTask(index: Int) {
        _subTasks.value.removeAt(index)
    }
}
