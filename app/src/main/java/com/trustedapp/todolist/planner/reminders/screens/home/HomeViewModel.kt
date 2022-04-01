package com.trustedapp.todolist.planner.reminders.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.nativead.NativeAd
import com.trustedapp.todolist.planner.reminders.data.repository.TaskRepository
import com.trustedapp.todolist.planner.reminders.initdata.createInitData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: TaskRepository) : ViewModel() {
    val exitNativeAd : StateFlow<NativeAd?> get() = _exitNativeAds
    private val _exitNativeAds = MutableStateFlow<NativeAd?>(null)

    fun setExitNativeAds(nativeAd: NativeAd){
        _exitNativeAds.value = nativeAd
    }

    fun createInitData() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            createInitData(repository)
        }
    }
}
