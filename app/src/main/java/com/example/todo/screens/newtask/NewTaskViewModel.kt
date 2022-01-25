package com.example.todo.screens.newtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.models.entity.SubTaskEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class NewTaskViewModel @Inject constructor() : ViewModel() {

    val subTasks: StateFlow<List<SubTaskEntity>> get() = _subTasks
    private val _subTasks = MutableStateFlow<MutableList<SubTaskEntity>>(mutableListOf())

    val canAddSubTask: StateFlow<Boolean> get() = _canAddSubTask
    private val _canAddSubTask = MutableStateFlow(false)

    init {
        addSubTask()
        _canAddSubTask.value = false
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
        _canAddSubTask.value = true
    }

    fun updateSubTask(index: Int, title: String) = viewModelScope.launch {
        _subTasks.value[index].name = title
        _canAddSubTask.value = !(_subTasks.value.size == 1 && _subTasks.value[0].name.isEmpty())
    }

    fun removeSubTask(index: Int) {
        _subTasks.value.removeAt(index)
    }
}
