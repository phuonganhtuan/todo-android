package com.example.todo.common.calendarview

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.data.models.model.DateModel
import com.example.todo.utils.DateTimeUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.Calendar.MONTH
import javax.inject.Inject

@AndroidEntryPoint
class TaskCalendarView : ConstraintLayout {

    private var buttonNextMonth: ImageView? = null
    private var buttonPreviousMonth: ImageView? = null
    private var textMonthYear: TextView? = null
    private var recyclerCalendar: RecyclerView? = null

    private var selectingMonth = Calendar.getInstance()
    private var selectingDate = Calendar.getInstance().time
    private var increasingId = 0

    var onDateSelectedListener: ((Date) -> Unit)? = null

    @Inject
    lateinit var adapter: CalendarAdapter

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    private fun initView() {

        inflate(context, R.layout.layout_calendar, this)
        buttonNextMonth = findViewById(R.id.imageNextMonth)
        buttonPreviousMonth = findViewById(R.id.imagePreviousMonth)
        textMonthYear = findViewById(R.id.textMonthYear)
        recyclerCalendar = findViewById(R.id.recyclerCalendar)

        recyclerCalendar?.adapter = adapter
        adapter.selectedDate = selectingDate
        setupData()
        setupEvents()
    }

    private fun setupEvents() {
        buttonNextMonth?.setOnClickListener {
            selectingMonth.set(MONTH, selectingMonth.get(MONTH) + 1)
            setupData()
        }
        buttonPreviousMonth?.setOnClickListener {
            selectingMonth.set(MONTH, selectingMonth.get(MONTH) - 1)
            setupData()
        }
        adapter.dateSelectListener = {
            onDateSelectedListener?.let { listener -> listener(it) }
            selectingDate = it
            adapter.selectedDate = it
            adapter.notifyDataSetChanged()
        }
    }

    private fun setupData() {
        val days = DateTimeUtils.getDaysOfMonth(selectingMonth)
        adapter.submitList(days.map {
            increasingId += 1
            val isInMonth =
                Calendar.getInstance().apply { time = it }.get(MONTH) == selectingMonth.get(
                    MONTH
                )
            DateModel(
                id = increasingId,
                date = it,
                isInMonth = isInMonth,
            )
        })
        textMonthYear?.text = DateTimeUtils.getMonthYearString(context, selectingMonth)
    }
}
