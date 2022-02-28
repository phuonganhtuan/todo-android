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

    private var textDay1: TextView? = null
    private var textDay2: TextView? = null
    private var textDay3: TextView? = null
    private var textDay4: TextView? = null
    private var textDay5: TextView? = null
    private var textDay6: TextView? = null
    private var textDay7: TextView? = null

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
        pagerCalendar?.setCurrentItem(
            pageIndex.indexOf(DateTimeUtils.getComparableMonthString(date.time)),
            false
        )
//            pageAdapter.notifyDataSetChanged()
//        }
    }

    private fun initView() {
        inflate(context, R.layout.layout_calendar, this)
        buttonNextMonth = findViewById(R.id.imageNextMonth)
        buttonPreviousMonth = findViewById(R.id.imagePreviousMonth)
        textMonthYear = findViewById(R.id.textMonthYear)
        pagerCalendar = findViewById(R.id.pagerCalendar)

        textDay1 = findViewById(R.id.textDay1)
        textDay2 = findViewById(R.id.textDay2)
        textDay3 = findViewById(R.id.textDay3)
        textDay4 = findViewById(R.id.textDay4)
        textDay5 = findViewById(R.id.textDay5)
        textDay6 = findViewById(R.id.textDay6)
        textDay7 = findViewById(R.id.textDay7)

        pagerCalendar?.adapter = pageAdapter
        pageAdapter.selectedDate = selectingDate
        val dayTitle = DateTimeUtils.getCalendarDayTitle()
        textDay1?.text = dayTitle[0]
        textDay2?.text = dayTitle[1]
        textDay3?.text = dayTitle[2]
        textDay4?.text = dayTitle[3]
        textDay5?.text = dayTitle[4]
        textDay6?.text = dayTitle[5]
        textDay7?.text = dayTitle[6]
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
            when (DateTimeUtils.compareInMonth(
                Calendar.getInstance().apply { time = it },
                selectingMonth
            )) {
                1 -> buttonNextMonth?.performClick()
                -1 -> buttonPreviousMonth?.performClick()
            }
        }
        pagerCalendar?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectingMonth = months[position]
                textMonthYear?.text = DateTimeUtils.getMonthYearString(selectingMonth)
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
        textMonthYear?.text = DateTimeUtils.getMonthYearString(selectingMonth)
    }
}
