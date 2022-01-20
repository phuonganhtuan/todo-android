package com.example.todo.screens.home.tasks

import androidx.lifecycle.ViewModel
import com.example.todo.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(private val repo: MainRepository) : ViewModel() {
}
