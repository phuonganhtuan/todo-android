package com.trustedapp.todolist.planner.reminders.screens.home.calendar

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trustedapp.todolist.planner.reminders.alarm.ScheduleHelper
import com.trustedapp.todolist.planner.reminders.data.models.entity.TaskShort
import com.trustedapp.todolist.planner.reminders.data.models.model.DateModel
import com.trustedapp.todolist.planner.reminders.data.repository.TaskRepository
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.Calendar.MONTH
import java.util.Calendar.getInstance
import javax.inject.Inject

@HiltViewModel
class CalendarTaskViewModel @Inject constructor(private val repository: TaskRepository) :
    ViewModel() {

    val selectingMonth: StateFlow<Calendar> get() = _selectingMonth
    private val _selectingMonth = MutableStateFlow(Calendar.getInstance())

    val selectedDay: StateFlow<Date> get() = _selectedDay
    private val _selectedDay = MutableStateFlow(Calendar.getInstance().time)

    val tasks: StateFlow<List<TaskShort>> get() = _tasks
    private val _tasks = MutableStateFlow(emptyList<TaskShort>())

    val months: StateFlow<List<Calendar>> get() = _months
    private val _months = MutableStateFlow(emptyList<Calendar>())

    init {
        getTasks(_selectedDay.value)
    }

    fun setMonth(month: Calendar) {
        _selectingMonth.value = month
    }

    fun setupMonths() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val monthLimit = 120
            val past = mutableListOf<Calendar>()
            val future = mutableListOf<Calendar>()
            val months = mutableListOf<Calendar>()
            for (i in 1 until monthLimit) {
                past.add(getInstance().apply { add(MONTH, i * -1) })
            }
            past.reverse()
            for (i in 1 until monthLimit) {
                future.add(getInstance().apply { add(MONTH, i) })
            }
            months.addAll(past)
            months.add(getInstance())
            months.addAll(future)
            _months.value = months
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
