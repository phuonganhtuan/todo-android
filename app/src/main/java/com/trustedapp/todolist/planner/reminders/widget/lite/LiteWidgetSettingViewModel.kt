package com.trustedapp.todolist.planner.reminders.widget.lite

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.data.models.entity.CategoryEntity
import com.trustedapp.todolist.planner.reminders.data.models.entity.StandardWidgetModel
import com.trustedapp.todolist.planner.reminders.data.models.model.DateModel
import com.trustedapp.todolist.planner.reminders.data.repository.TaskRepository
import com.trustedapp.todolist.planner.reminders.data.repository.WidgetRepository
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import com.trustedapp.todolist.planner.reminders.utils.getStringByLocale
import com.trustedapp.todolist.planner.reminders.widget.standard.WidgetItemWrap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class LiteWidgetSettingViewModel @Inject constructor(
    private val repository: WidgetRepository,
    private val taskRepository: TaskRepository
) :
    ViewModel() {

    val widgetModel: StateFlow<StandardWidgetModel> get() = _widgetModel
    private val _widgetModel = MutableStateFlow(
        StandardWidgetModel(
            isOnlyToday = true, containCompleted = false
        )
    )

    private var widgetId = 0

    val days: StateFlow<List<DateModel>> get() = _days
    private val _days = MutableStateFlow(emptyList<DateModel>())

    val categories: StateFlow<List<CategoryEntity>> get() = _categories
    private val _categories = taskRepository
        .getCategories()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(2000),
            emptyList()
        )

    val tasks: StateFlow<List<WidgetItemWrap>> get() = _tasks
    private val _tasks = MutableStateFlow(emptyList<WidgetItemWrap>())

    fun getTasks() {

    }

    fun getPreviewData(
        context: Context,
        containCompleted: Boolean,
        categoryId: Int?,
        isOnlyToday: Boolean
    ) = viewModelScope.launch {
        var todayTasks = mutableListOf<WidgetItemWrap>()
        todayTasks = if (containCompleted) {
            taskRepository.getTaskInDayAll(
                DateTimeUtils.getComparableDateString(
                    Calendar.getInstance().time,
                    isDefault = true
                ),
                DateTimeUtils.getStartOfDay(Calendar.getInstance().time),
                DateTimeUtils.getStartOfNextDay(Calendar.getInstance().time).time
            )
                .map { WidgetItemWrap(task = it) }.toMutableList()
        } else {
            taskRepository.getTaskInDay(
                DateTimeUtils.getComparableDateString(
                    Calendar.getInstance().time,
                    isDefault = true
                ),
                DateTimeUtils.getStartOfDay(Calendar.getInstance().time),
                DateTimeUtils.getStartOfNextDay(Calendar.getInstance().time).time
            )
                .map { WidgetItemWrap(task = it) }.toMutableList()
        }
        if (categoryId != null) {
            todayTasks =
                todayTasks.filter { item -> item.task == null || item.task?.task?.categoryId == categoryId }
                    .toMutableList()
        }
        if (todayTasks.isEmpty()) {
            todayTasks.add(
                WidgetItemWrap(
                    task = null,
                    isHeader = !isOnlyToday,
                    header = context.getStringByLocale(R.string.today)
                )
            )
        } else {
            todayTasks[0].apply {
                isHeader = !isOnlyToday
                header = context.getStringByLocale(R.string.today)
            }
        }
        if (!isOnlyToday) {
            var future = if (containCompleted) {
                taskRepository.getFutureTaskAll(DateTimeUtils.getTomorrow())
                    .map { WidgetItemWrap(task = it, isOther = true) }
                    .toMutableList()
            } else {
                taskRepository.getFutureTask(DateTimeUtils.getTomorrow())
                    .map { WidgetItemWrap(task = it, isOther = true) }
                    .toMutableList()
            }
            if (categoryId != null) {
                future =
                    future.filter { item -> item.task == null || item.task?.task?.categoryId == categoryId }
                        .toMutableList()
            }
            if (future.isEmpty()) {
                future.add(
                    WidgetItemWrap(
                        task = null,
                        isHeader = !isOnlyToday,
                        header = context.getStringByLocale(R.string.future_task)
                    )
                )
            } else {
                future[0].apply {
                    isHeader = !isOnlyToday
                    header = context.getStringByLocale(R.string.future_task)
                }
            }
            todayTasks.addAll(future)
        }
        _tasks.value = todayTasks
    }

    fun getData(id: Int) = viewModelScope.launch {
        widgetId = id
        _widgetModel.value = repository.getStandardWidgetModel(id) ?: StandardWidgetModel(
            isOnlyToday = true, containCompleted = false
        )
    }

    fun saveData(
        color: Int,
        alpha: Int,
        isDark: Boolean,
        isOnlyToday: Boolean,
        categoryId: Int?,
        containCompleted: Boolean
    ) = viewModelScope.launch {
        val entity =
            StandardWidgetModel(
                id = _widgetModel.value.id,
                widgetId = widgetId,
                color = color,
                alpha = alpha,
                isDark = isDark,
                isOnlyToday = isOnlyToday,
                categoryId = categoryId,
                containCompleted = containCompleted,
            )
        repository.insertStandardWidgetModel(entity)
    }
}
