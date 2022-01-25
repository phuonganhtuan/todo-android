package com.example.todo.screens.newtask

import androidx.lifecycle.ViewModel
import com.example.todo.data.models.entity.SubTaskEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class NewTaskViewModel @Inject constructor() : ViewModel() {

    val subTasks: StateFlow<List<SubTaskEntity>> get() = _subTasks
    private val _subTasks = MutableStateFlow<MutableList<SubTaskEntity>>(mutableListOf())

    init {
        addSubTask()
    }

    fun addSubTask() {
        val currentSubTasks = _subTasks.value.toMutableList()
        currentSubTasks.add(SubTaskEntity(name = "", isDone = false, taskId = 0, id = Random.nextInt()))
        _subTasks.value = currentSubTasks
    }

    fun removeSubTask(index: Int) {
        _subTasks.value.removeAt(index)
    }
}
