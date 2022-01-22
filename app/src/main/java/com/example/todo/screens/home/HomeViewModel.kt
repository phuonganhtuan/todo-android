package com.example.todo.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.repository.TaskRepository
import com.example.todo.demo.createDemoData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: TaskRepository) : ViewModel() {

    fun createDemoData() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            createDemoData(repository)
        }
    }
}
