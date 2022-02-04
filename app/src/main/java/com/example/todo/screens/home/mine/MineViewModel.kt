package com.example.todo.screens.home.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.models.entity.TaskShort
import com.example.todo.data.repository.TaskRepository
import com.example.todo.utils.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.*
import java.util.Calendar.DAY_OF_WEEK
import javax.inject.Inject

@HiltViewModel
class MineViewModel @Inject constructor(private val repository: TaskRepository) : ViewModel() {

    val completedTasksCount: Flow<Int> get() = _tasks.map {
        it.filter { task -> task.task.isDone }.size
    }

    val pendingTasksCount: Flow<Int> get() = _tasks.map {
        it.filter { task -> !task.task.isDone }.size
    }

    val next7DaysTasks: Flow<List<TaskShort>>
        get() = _tasks.map {
            val next7Days = mutableListOf<String>()
            val calendar = Calendar.getInstance()
            for (i in 0 until 7) {
                next7Days.add(DateTimeUtils.getComparableDateString(calendar.time))
                calendar.add(DAY_OF_WEEK, 1)
            }
            it.filter { task ->
                next7Days.contains(
                    DateTimeUtils.getComparableDateString(
                        Calendar.getInstance().apply { timeInMillis = task.task.calendar ?: 0 }.time
                    )
                ) && !task.task.isDone
            }
        }

    private val _tasks = repository.getShortTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())
}
