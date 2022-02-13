package com.trustedapp.todolist.planner.reminders.screens.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trustedapp.todolist.planner.reminders.data.models.entity.TaskEntity
import com.trustedapp.todolist.planner.reminders.data.repository.TaskRepository
import com.trustedapp.todolist.planner.reminders.utils.SPUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchTaskViewModel @Inject constructor(private val repository: TaskRepository) :
    ViewModel() {

    val tasks: StateFlow<List<TaskEntity>> get() = _tasks
    private val _tasks = MutableStateFlow(emptyList<TaskEntity>())

    val recentSearch: StateFlow<List<String>> get() = _recentSearch
    private val _recentSearch = MutableStateFlow(listOf<String>())

    fun search(taskName: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _tasks.value = if (taskName.isEmpty()) {
                mutableListOf()
            } else {
                repository.searchTaskByName(taskName)
            }
        }
    }

    fun getRecentSearch(context: Context) {
        _recentSearch.value = SPUtils.getRecentSearch(context).toMutableList()
    }

    fun addRecentSearch(context: Context, name: String) = viewModelScope.launch {
        val currentRecent = _recentSearch.value.toMutableList()
        currentRecent.apply {
            if (contains(name)) remove(name)
            add(0, name)
            if (size > 3) removeAt(size - 1)
        }
        SPUtils.saveRecentSearch(context, currentRecent)
    }
}
