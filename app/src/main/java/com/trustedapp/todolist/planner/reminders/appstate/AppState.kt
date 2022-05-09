package com.trustedapp.todolist.planner.reminders.appstate

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AppState {
    val isCreatedTask: StateFlow<Boolean> get() = _isCreatedTask
    private val _isCreatedTask = MutableStateFlow(false)

    fun setIsCreatedTask(value: Boolean) {
        _isCreatedTask.value = value
    }

}