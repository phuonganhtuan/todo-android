package com.trustedapp.todolist.planner.reminders.screens.home.calendar

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.trustedapp.todolist.planner.reminders.data.datasource.local.database.AppDatabase
import com.trustedapp.todolist.planner.reminders.data.datasource.local.impl.TaskLocalDataSourceImpl
import com.trustedapp.todolist.planner.reminders.data.models.model.DateModel
import com.trustedapp.todolist.planner.reminders.data.repository.TaskRepository
import com.trustedapp.todolist.planner.reminders.data.repository.impl.TaskRepositoryImpl
import com.trustedapp.todolist.planner.reminders.databinding.ItemCalendarTaskPageBinding
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class CalendarPageAdapter @Inject constructor() : RecyclerView.Adapter<CalendarPageViewHolder>() {

    var listMonth = listOf<Calendar>()

    var dateSelectListener: ((Date) -> Unit)? = null

    var selectedDate = Calendar.getInstance().time

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarPageViewHolder {
        val itemViewBinding =
            ItemCalendarTaskPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarPageViewHolder(itemViewBinding, dateSelectListener)
    }

    override fun getItemCount() = listMonth.size

    override fun onBindViewHolder(holder: CalendarPageViewHolder, position: Int) {
        holder.display(listMonth[position], selectedDate)
    }
}

class CalendarPageViewHolder(
    private val itemViewBinding: ItemCalendarTaskPageBinding,
    private val dateSelectListener: ((Date) -> Unit)?
) :
    RecyclerView.ViewHolder(itemViewBinding.root) {

    private var adapter = CalendarTaskAdapter()

    private var currentIndex = -1

    private var viewModel = CalendarPageViewModel(
        TaskRepositoryImpl(
            TaskLocalDataSourceImpl(
                AppDatabase.invoke(itemView.context).taskDao()
            )
        )
    )

    private var selectedDate = Calendar.getInstance().time

    init {
        itemViewBinding.recyclerCalendar.adapter = adapter
        adapter.dateSelectListener = {
            if (currentIndex != -1) {
                adapter.notifyItemChanged(currentIndex)
            }
            currentIndex =
                viewModel.days.value.map { item -> DateTimeUtils.getComparableDateString(item.date) }
                    .indexOf(DateTimeUtils.getComparableDateString(it))
            dateSelectListener?.let { listener -> listener(it) }
        }
        itemViewBinding.recyclerCalendar.itemAnimator = null
        viewModel.viewModelScope.launch {
            viewModel.days.collect {
                currentIndex = it.map { item -> DateTimeUtils.getComparableDateString(item.date) }
                    .indexOf(DateTimeUtils.getComparableDateString(selectedDate))
                if (it.isNotEmpty()) {
                    adapter.submitList(it)
                }
                if (currentIndex != -1) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        adapter.notifyItemChanged(currentIndex)
                    }, 100)
                }
            }
        }
    }

    fun display(item: Calendar, selectedDate: Date) = with(itemViewBinding) {
        adapter.selectedDate = selectedDate
        this@CalendarPageViewHolder.selectedDate = selectedDate
        if (currentIndex != -1) {
            adapter.notifyItemChanged(currentIndex)
        }
        Handler(Looper.getMainLooper()).post {
            viewModel.setupData(item)
        }
    }
}

@HiltViewModel
class CalendarPageViewModel @Inject constructor(private val repository: TaskRepository) :
    ViewModel() {

    val days: StateFlow<List<DateModel>> get() = _days
    private val _days = MutableStateFlow(emptyList<DateModel>())

    fun setupData(month: Calendar) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _days.value = emptyList()
            _days.value = DateTimeUtils.getDaysOfMonth(month).map {
                val isInMonth =
                    Calendar.getInstance().apply { time = it }
                        .get(Calendar.MONTH) == month.get(
                        Calendar.MONTH
                    )
                val tasks = repository.getTaskInDay(DateTimeUtils.getComparableDateString(it, isDefault = true), DateTimeUtils.getStartOfDay(it), DateTimeUtils.getStartOfNextDay(it).time)
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
}
