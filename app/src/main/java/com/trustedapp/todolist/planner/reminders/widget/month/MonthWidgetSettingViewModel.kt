package com.trustedapp.todolist.planner.reminders.widget.month

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trustedapp.todolist.planner.reminders.data.models.entity.MonthWidgetModel
import com.trustedapp.todolist.planner.reminders.data.models.model.DateModel
import com.trustedapp.todolist.planner.reminders.data.repository.TaskRepository
import com.trustedapp.todolist.planner.reminders.data.repository.WidgetRepository
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class MonthWidgetSettingViewModel @Inject constructor(
    private val repository: WidgetRepository,
    private val taskRepository: TaskRepository
) :
    ViewModel() {

    val widgetModel: StateFlow<MonthWidgetModel> get() = _widgetModel
    private val _widgetModel = MutableStateFlow(MonthWidgetModel())

    private var widgetId = 0

    val days: StateFlow<List<DateModel>> get() = _days
    private val _days = MutableStateFlow(emptyList<DateModel>())

    fun setupDays(month: Calendar) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _days.value = emptyList()
            _days.value = DateTimeUtils.getDaysOfMonth(month).map {
                val isInMonth =
                    Calendar.getInstance().apply { time = it }
                        .get(Calendar.MONTH) == month.get(
                        Calendar.MONTH
                    )
                val tasks = taskRepository.getTaskInDay(
                    DateTimeUtils.getComparableDateString(
                        it,
                        isDefault = true
                    ),
                    DateTimeUtils.getStartOfDay(it),
                    DateTimeUtils.getStartOfNextDay(it).time
                )
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

    fun getData(id: Int) = viewModelScope.launch {
        widgetId = id
        _widgetModel.value = repository.getMonthWidgetModel(id) ?: MonthWidgetModel()
    }

    fun saveData(color: Int, alpha: Int, isDark: Boolean) = viewModelScope.launch {
        val entity =
            MonthWidgetModel(
                id = _widgetModel.value.id,
                widgetId = widgetId,
                color = color,
                alpha = alpha,
                isDark = isDark,
            )
        repository.insertMonthWidgetModel(entity)
    }
}
