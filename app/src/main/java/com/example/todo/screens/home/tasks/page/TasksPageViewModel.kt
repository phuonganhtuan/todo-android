package com.example.todo.screens.home.tasks.page

import android.text.format.DateUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.models.entity.TaskShort
import com.example.todo.data.repository.TaskRepository
import com.example.todo.utils.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
@ExperimentalCoroutinesApi
class TasksPageViewModel @Inject constructor(private val repository: TaskRepository) : ViewModel() {

    val todayTasks: Flow<List<TaskShort>>
        get() = allTasks.mapLatest {
            it.filter { task ->
                DateUtils.isToday(task.task.calendar ?: 0L) && !task.task.isDone
            }
        }

    val futureTasks: Flow<List<TaskShort>>
        get() = allTasks.mapLatest {
            it.filter { task ->
                (task.task.calendar ?: 0L) >= DateTimeUtils.getTomorrow() && !task.task.isDone
            }
        }

    val doneTasks: Flow<List<TaskShort>>
        get() = allTasks.mapLatest {
            it.filter { task -> task.task.isDone }
        }

    private val allTasks = repository
        .getShortTasks()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(2000),
            emptyList()
        )
}
