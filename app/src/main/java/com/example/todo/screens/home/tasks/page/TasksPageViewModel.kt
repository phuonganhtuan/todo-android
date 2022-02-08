package com.example.todo.screens.home.tasks.page

import android.content.Context
import android.text.format.DateUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.alarm.ScheduleHelper
import com.example.todo.data.models.entity.TaskShort
import com.example.todo.data.repository.TaskRepository
import com.example.todo.utils.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
@ExperimentalCoroutinesApi
class TasksPageViewModel @Inject constructor(private val repository: TaskRepository) : ViewModel() {

    val todayTasks: Flow<List<TaskShort>>
        get() = allTasks.map {
            it.filter { task ->
                DateUtils.isToday(task.task.calendar ?: 0L) && !task.task.isDone
            }
        }

    val futureTasks: Flow<List<TaskShort>>
        get() = allTasks.map {
            it.filter { task ->
                (task.task.calendar ?: 0L) >= DateTimeUtils.getTomorrow() && !task.task.isDone
            }
        }

    val doneTasks: Flow<List<TaskShort>>
        get() = allTasks.map {
            it.filter { task -> task.task.isDone }
        }

    private val allTasks = repository
        .getShortTasks()
        .map { it.reversed() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(2000),
            emptyList()
        )

    fun updateStatus(context: Context, id: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val taskShortToUpdate = allTasks.value.filter { it.task.id == id }[0]
            val taskToUpdate = taskShortToUpdate.task.copy()
            taskToUpdate.isDone = !taskToUpdate.isDone
            repository.updateTask(taskToUpdate)
            if (taskToUpdate.isDone) {
                ScheduleHelper.cancelAlarm(context, taskToUpdate.id)
            }
        }
    }

    fun updateMark(id: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val taskShortToUpdate = allTasks.value.filter { it.task.id == id }[0]
            val taskToUpdate = taskShortToUpdate.task.copy()
            taskToUpdate.isMarked = !taskToUpdate.isMarked
            repository.updateTask(taskToUpdate)
        }
    }
}
