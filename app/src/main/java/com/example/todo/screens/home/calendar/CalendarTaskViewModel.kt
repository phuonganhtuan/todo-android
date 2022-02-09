package com.example.todo.screens.home.calendar

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.alarm.ScheduleHelper
import com.example.todo.data.models.entity.TaskShort
import com.example.todo.data.models.model.DateModel
import com.example.todo.data.repository.TaskRepository
import com.example.todo.utils.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalendarTaskViewModel @Inject constructor(private val repository: TaskRepository) :
    ViewModel() {

    val selectingMonth: StateFlow<Calendar> get() = _selectingMonth
    private val _selectingMonth = MutableStateFlow(Calendar.getInstance())

    val selectedDay: StateFlow<Date> get() = _selectedDay
    private val _selectedDay = MutableStateFlow(Calendar.getInstance().time)

    val days: StateFlow<List<DateModel>> get() = _days
    private val _days = MutableStateFlow(emptyList<DateModel>())

    val tasks: StateFlow<List<TaskShort>> get() = _tasks
    private val _tasks = MutableStateFlow(emptyList<TaskShort>())

    init {
        getTasks(_selectedDay.value)
    }

    fun setupData() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _days.value = DateTimeUtils.getDaysOfMonth(_selectingMonth.value).map {
                val isInMonth =
                    Calendar.getInstance().apply { time = it }
                        .get(Calendar.MONTH) == _selectingMonth.value.get(
                        Calendar.MONTH
                    )
                val tasks = repository.getTaskInDay(DateTimeUtils.getComparableDateString(it))
                val tasksCatName = tasks.map { t -> t.category?.name.toString().lowercase() }
                DateModel(
                    id = it.time.toInt(),
                    date = it,
                    isInMonth = isInMonth,
                    hasTask = tasks.isNotEmpty(),
                    hasBirthday = tasksCatName.contains("birthday")
                )
            }
        }
    }

    fun getTasks(day: Date) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _selectedDay.value = day
            _tasks.value = repository.getTaskInDay(DateTimeUtils.getComparableDateString(day))
        }
    }

    fun nextMonth() {
        val month = Calendar.getInstance().apply { time = _selectingMonth.value.time }
        month.set(Calendar.MONTH, _selectingMonth.value.get(Calendar.MONTH) + 1)
        _selectingMonth.value = month
    }

    fun previousMonth() {
        val month = Calendar.getInstance().apply { time = _selectingMonth.value.time }
        month.set(Calendar.MONTH, _selectingMonth.value.get(Calendar.MONTH) - 1)
        _selectingMonth.value = month
    }

    fun updateStatus(context: Context, id: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val taskShortToUpdate = _tasks.value.filter { it.task.id == id }[0]
            val taskToUpdate = taskShortToUpdate.task.copy()
            taskToUpdate.isDone = !taskToUpdate.isDone
            repository.updateTask(taskToUpdate)
            val reminder = repository.getReminder(taskToUpdate.id)
            if (reminder != null) {
                if (taskToUpdate.isDone) {
                    ScheduleHelper.cancelAlarm(context, taskToUpdate, reminder)
                } else {
                    ScheduleHelper.addAlarm(context, taskToUpdate, reminder)
                }
            }
            _tasks.value =
                repository.getTaskInDay(DateTimeUtils.getComparableDateString(_selectedDay.value))
        }
    }

    fun updateMark(id: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val taskShortToUpdate = _tasks.value.filter { it.task.id == id }[0]
            val taskToUpdate = taskShortToUpdate.task.copy()
            taskToUpdate.isMarked = !taskToUpdate.isMarked
            repository.updateTask(taskToUpdate)
            _tasks.value =
                repository.getTaskInDay(DateTimeUtils.getComparableDateString(_selectedDay.value))
        }
    }
}
