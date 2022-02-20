package com.trustedapp.todolist.planner.reminders.common.calendarview

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import java.util.*
import java.util.Calendar.MONTH
import javax.inject.Inject

@AndroidEntryPoint
class TaskCalendarView : ConstraintLayout {

    private var buttonNextMonth: ImageView? = null
    private var buttonPreviousMonth: ImageView? = null
    private var textMonthYear: TextView? = null
    private var pagerCalendar: ViewPager2? = null

    private var selectingMonth = Calendar.getInstance()
    private var selectingDate = Calendar.getInstance().time
    private var months = listOf<Calendar>()

    var onDateSelectedListener: ((Date) -> Unit)? = null

    @Inject
    lateinit var pageAdapter: CalendarViewPageAdapter

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

    fun setDate(date: Calendar) {
        selectingDate = date.time
        selectingMonth = date
        pageAdapter.selectedDate = date.time
        val pageIndex = months.map { DateTimeUtils.getComparableMonthString(it.time) }
//        if ((pagerCalendar?.currentItem ?: 0) != pageIndex) {
            pagerCalendar?.setCurrentItem(pageIndex.indexOf(DateTimeUtils.getComparableMonthString(date.time)), false)
//            pageAdapter.notifyDataSetChanged()
//        }
    }

    private fun initView() {
        inflate(context, R.layout.layout_calendar, this)
        buttonNextMonth = findViewById(R.id.imageNextMonth)
        buttonPreviousMonth = findViewById(R.id.imagePreviousMonth)
        textMonthYear = findViewById(R.id.textMonthYear)
        pagerCalendar = findViewById(R.id.pagerCalendar)

        pagerCalendar?.adapter = pageAdapter
        pageAdapter.selectedDate = selectingDate
        setupData()
        setupEvents()
    }

    private fun setupEvents() {
        buttonNextMonth?.setOnClickListener {
            try {
                pagerCalendar?.currentItem = (pagerCalendar?.currentItem ?: 0) + 1
            } catch (exception: Exception) {

            }
        }
        buttonPreviousMonth?.setOnClickListener {
            try {
                pagerCalendar?.currentItem = (pagerCalendar?.currentItem ?: 0) - 1
            } catch (exception: Exception) {

            }
        }
        pageAdapter.dateSelectListener = {
            onDateSelectedListener?.let { listener -> listener(it) }
            selectingDate = it
            pageAdapter.selectedDate = it
            pageAdapter.notifyDataSetChanged()
            when (DateTimeUtils.compareInMonth(Calendar.getInstance().apply { time = it }, selectingMonth))  {
                1 -> buttonNextMonth?.performClick()
                -1 -> buttonPreviousMonth?.performClick()
            }
        }
        pagerCalendar?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectingMonth = months[position]
                textMonthYear?.text = DateTimeUtils.getMonthYearString(context, selectingMonth)
            }
        })
    }

    private fun setupData() {
        val monthLimit = 300
        val past = mutableListOf<Calendar>()
        val future = mutableListOf<Calendar>()
        val months = mutableListOf<Calendar>()
        for (i in 1 until monthLimit) {
            past.add(Calendar.getInstance().apply { add(MONTH, i * -1) })
        }
        past.reverse()
        for (i in 1 until monthLimit) {
            future.add(Calendar.getInstance().apply { add(MONTH, i) })
        }
        months.addAll(past)
        months.add(Calendar.getInstance())
        months.addAll(future)
        this.months = months
        pageAdapter.listMonth = months
        pageAdapter.notifyDataSetChanged()
        val index = months.map { cal -> DateTimeUtils.getComparableMonthString(cal.time) }
            .indexOf(DateTimeUtils.getComparableMonthString(Calendar.getInstance().time))
        pagerCalendar?.setCurrentItem(index, false)
        textMonthYear?.text = DateTimeUtils.getMonthYearString(context, selectingMonth)
    }
}
