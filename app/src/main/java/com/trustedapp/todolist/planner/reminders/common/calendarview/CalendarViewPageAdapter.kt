package com.trustedapp.todolist.planner.reminders.common.calendarview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.trustedapp.todolist.planner.reminders.data.models.model.DateModel
import com.trustedapp.todolist.planner.reminders.databinding.ItemCalendarTaskPageBinding
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class CalendarViewPageAdapter @Inject constructor() :
    RecyclerView.Adapter<CalendarViewPageViewHolder>() {

    var listMonth = listOf<Calendar>()

    var dateSelectListener: ((Date) -> Unit)? = null

    var selectedDate = Calendar.getInstance().time

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewPageViewHolder {
        val itemViewBinding =
            ItemCalendarTaskPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewPageViewHolder(itemViewBinding, dateSelectListener)
    }

    override fun getItemCount() = listMonth.size

    override fun onBindViewHolder(holder: CalendarViewPageViewHolder, position: Int) {
        holder.display(listMonth[position], selectedDate)
    }
}

class CalendarViewPageViewHolder(
    private val itemViewBinding: ItemCalendarTaskPageBinding,
    private val dateSelectListener: ((Date) -> Unit)?
) :
    RecyclerView.ViewHolder(itemViewBinding.root) {

    private var adapter = CalendarAdapter()

    private var currentIndex = -1

    private var viewModel = CalendarPageViewModel()

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
            if (currentIndex != -1) {
                adapter.notifyItemChanged(currentIndex)
            }
            dateSelectListener?.let { listener -> listener(it) }
        }
        itemViewBinding.recyclerCalendar.itemAnimator = null
    }

    fun display(item: Calendar, selectedDate: Date) = with(itemViewBinding) {
        adapter.selectedDate = selectedDate
        this@CalendarViewPageViewHolder.selectedDate = selectedDate
        if (currentIndex != -1) {
            adapter.notifyItemChanged(currentIndex)
        }
        val days = viewModel.setupData(item)
        if (days.isNotEmpty()) {
            adapter.submitList(days)
        }
        currentIndex = days.map { item -> DateTimeUtils.getComparableDateString(item.date) }
            .indexOf(DateTimeUtils.getComparableDateString(selectedDate))
        if (currentIndex != -1) {
            adapter.notifyItemChanged(currentIndex)
        }
    }
}

@HiltViewModel
class CalendarPageViewModel @Inject constructor() :
    ViewModel() {

    val days: StateFlow<List<DateModel>> get() = _days
    private val _days = MutableStateFlow(emptyList<DateModel>())

    private var increasingId = 0

    fun setupData(month: Calendar) = DateTimeUtils.getDaysOfMonth(month).map {
        increasingId += 1
        val isInMonth =
            Calendar.getInstance().apply { time = it }
                .get(Calendar.MONTH) == month.get(
                Calendar.MONTH
            )
        DateModel(
            id = increasingId,
            date = it,
            isInMonth = isInMonth,
        )
    }
}
