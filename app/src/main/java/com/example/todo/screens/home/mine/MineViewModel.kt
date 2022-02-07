package com.example.todo.screens.home.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.common.chart.ChartColor
import com.example.todo.data.models.entity.DefaultCategories
import com.example.todo.data.models.entity.TaskShort
import com.example.todo.data.models.model.ChartStatisticModel
import com.example.todo.data.repository.TaskRepository
import com.example.todo.utils.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.*
import java.util.Calendar.DAY_OF_WEEK
import javax.inject.Inject

@HiltViewModel
class MineViewModel @Inject constructor(private val repository: TaskRepository) : ViewModel() {

    val completedTasksCount: Flow<Int>
        get() = _tasks.map {
            it.filter { task -> task.task.isDone }.size
        }

    val pendingTasksCount: Flow<Int>
        get() = _tasks.map {
            it.filter { task -> !task.task.isDone }.size
        }

    val chartData: Flow<List<ChartStatisticModel>>
        get() = _tasks.map {
            if (_tasks.value.isEmpty()) {
                listOf()
            } else {
                val catStatisticList = mutableListOf<ChartStatisticModel>()
                val workCount = _tasks.value.filter { item ->
                    item.category != null && item.category!!.name.lowercase() ==
                            DefaultCategories.WORK.name.lowercase()
                }.size
                val workModel = ChartStatisticModel(
                    name = DefaultCategories.WORK.name,
                    value = workCount.toString(),
                    color = ChartColor.chartColors[1],
                    percent = (workCount / _tasks.value.size.toFloat()) * 100f,
                    id = 1
                )
                val personalCount = _tasks.value.filter { item ->
                    item.category != null && item.category!!.name.lowercase() ==
                            DefaultCategories.PERSONAL.name.lowercase()
                }.size
                val personalModel = ChartStatisticModel(
                    name = DefaultCategories.PERSONAL.name,
                    value = personalCount.toString(),
                    color = ChartColor.chartColors[2],
                    percent = (personalCount / _tasks.value.size.toFloat()) * 100f,
                    id = 2
                )
                val birthdayCount = _tasks.value.filter { item ->
                    item.category != null && item.category!!.name.lowercase() ==
                            DefaultCategories.BIRTHDAY.name.lowercase()
                }.size
                val birthdayModel = ChartStatisticModel(
                    name = DefaultCategories.BIRTHDAY.name,
                    value = birthdayCount.toString(),
                    color = ChartColor.chartColors[3],
                    percent = (birthdayCount / _tasks.value.size.toFloat()) * 100f,
                    id = 3
                )
                val wishListCount = _tasks.value.filter { item ->
                    item.category != null && item.category!!.name.lowercase() ==
                            DefaultCategories.WISHLIST.name.lowercase()
                }.size
                val wishListModel = ChartStatisticModel(
                    name = DefaultCategories.WISHLIST.name,
                    value = wishListCount.toString(),
                    color = ChartColor.chartColors[4],
                    percent = (wishListCount / _tasks.value.size.toFloat()) * 100f,
                    id = 4
                )
                val noCatCount = _tasks.value.filter { item ->
                    item.category == null
                }.size
                val noCatModel = ChartStatisticModel(
                    name = "",
                    value = noCatCount.toString(),
                    color = ChartColor.chartColors[0],
                    percent = (noCatCount / _tasks.value.size.toFloat()) * 100f,
                    id = 0
                )
                val otherCount = _tasks.value.filter { item ->
                    item.category != null &&
                            item.category!!.name.lowercase() != DefaultCategories.WISHLIST.name.lowercase() &&
                            item.category!!.name.lowercase() != DefaultCategories.WORK.name.lowercase() &&
                            item.category!!.name.lowercase() != DefaultCategories.BIRTHDAY.name.lowercase() &&
                            item.category!!.name.lowercase() != DefaultCategories.PERSONAL.name.lowercase()
                }.size
                val otherModel = ChartStatisticModel(
                    name = "other",
                    value = otherCount.toString(),
                    color = ChartColor.chartColors[5],
                    percent = (otherCount / _tasks.value.size.toFloat()) * 100f,
                    id = 5
                )
                catStatisticList.apply {
                    add(noCatModel)
                    add(workModel)
                    add(personalModel)
                    add(birthdayModel)
                    add(wishListModel)
                    add(otherModel)
                }
                catStatisticList.toList()
            }
        }

    val next7DaysTasks: Flow<List<TaskShort>>
        get() = _tasks.map {
            val next7Days = mutableListOf<String>()
            val calendar = Calendar.getInstance()
            for (i in 0 until 7) {
                next7Days.add(DateTimeUtils.getComparableDateString(calendar.time))
                calendar.add(DAY_OF_WEEK, 1)
            }
            it.filter { task ->
                next7Days.contains(
                    DateTimeUtils.getComparableDateString(
                        Calendar.getInstance().apply { timeInMillis = task.task.calendar ?: 0 }.time
                    )
                ) && !task.task.isDone
            }
        }

    val tasksCount: Flow<Int> get() = _tasks.map {
        it.size
    }
    private val _tasks = repository.getShortTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())
}
