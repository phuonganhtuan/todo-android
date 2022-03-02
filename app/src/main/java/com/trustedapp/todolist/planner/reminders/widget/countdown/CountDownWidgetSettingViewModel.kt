package com.trustedapp.todolist.planner.reminders.widget.countdown

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trustedapp.todolist.planner.reminders.data.models.entity.CountDownWidgetModel
import com.trustedapp.todolist.planner.reminders.data.repository.WidgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class CountDownWidgetSettingViewModel @Inject constructor(private val repository: WidgetRepository) :
    ViewModel() {

    val widgetModel: StateFlow<CountDownWidgetModel> get() = _widgetModel
    private val _widgetModel = MutableStateFlow(CountDownWidgetModel())

    private var widgetId = 0

    fun getData(id: Int) = viewModelScope.launch {
        widgetId = id
        _widgetModel.value = repository.getCountDownModel(id) ?: CountDownWidgetModel()
    }

    fun saveData(name: String, date: Long, iconIndex: Int, type: String) = viewModelScope.launch {
        val entity = if (_widgetModel.value.eventName.isEmpty()) {
            CountDownWidgetModel(
                widgetId = widgetId,
                date = date,
                eventName = name,
                countType = type,
                iconIndex = iconIndex,
                updateTime = System.currentTimeMillis()
            )
        } else {
            _widgetModel.value.apply {
                this.widgetId = this@CountDownWidgetSettingViewModel.widgetId
                this.date = date
                eventName = name
                countType = type
                this.iconIndex = iconIndex
                updateTime = System.currentTimeMillis()
            }
        }
        repository.insertCountDownModel(entity)
    }
}
