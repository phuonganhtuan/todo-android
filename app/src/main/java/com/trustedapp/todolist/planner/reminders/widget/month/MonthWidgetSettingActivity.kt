package com.trustedapp.todolist.planner.reminders.widget.month

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.data.datasource.local.database.AppDatabase
import com.trustedapp.todolist.planner.reminders.data.datasource.local.impl.TaskLocalDataSourceImpl
import com.trustedapp.todolist.planner.reminders.data.datasource.local.impl.WidgetDataSourceImpl
import com.trustedapp.todolist.planner.reminders.data.repository.impl.TaskRepositoryImpl
import com.trustedapp.todolist.planner.reminders.data.repository.impl.WidgetRepositoryImpl
import com.trustedapp.todolist.planner.reminders.databinding.ActivityWidgetMonthSettingBinding
import com.trustedapp.todolist.planner.reminders.screens.home.calendar.CalendarTaskAdapter
import com.trustedapp.todolist.planner.reminders.screens.theme.ThemeTextureColorAdapter
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import com.trustedapp.todolist.planner.reminders.utils.hide
import com.trustedapp.todolist.planner.reminders.widget.isWidgetLightContent
import com.trustedapp.todolist.planner.reminders.widget.widgetBgColors
import com.trustedapp.todolist.planner.reminders.widget.widgetBgs
import kotlinx.coroutines.flow.collect
import java.util.*

class MonthWidgetSettingActivity : BaseActivity<ActivityWidgetMonthSettingBinding>() {

    private val viewModel by lazy {
        MonthWidgetSettingViewModel(
            WidgetRepositoryImpl(
                WidgetDataSourceImpl(AppDatabase.invoke(this).taskDao())
            ),
            TaskRepositoryImpl(
                TaskLocalDataSourceImpl(AppDatabase.invoke(this).taskDao())
            )
        )
    }

    private var alpha = 100

    private var selectedColor = widgetBgColors[0]

    private val adapter: ThemeTextureColorAdapter by lazy {
        ThemeTextureColorAdapter()
    }

    private var selectedDate = Calendar.getInstance().time

    private var adapterCalendar = CalendarTaskAdapter()

    override fun inflateViewBinding() =
        ActivityWidgetMonthSettingBinding.inflate(layoutInflater)

    override fun onActivityReady(savedInstanceState: Bundle?) {
        val appWidgetId = intent.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultValue)
    }

    override fun onActivityReady() {
        initViews()
        initData()
        setupEvents()
        observeData()
    }

    private fun initViews() = with(viewBinding) {
        header.apply {
            button1.setBackgroundResource(R.drawable.ic_arrow_left)
            button2.hide()
            button3.hide()
            button4.hide()
            textTitle.text = getString(R.string.month)
        }
        recyclerColors.adapter = adapter
        recyclerCalendar.adapter = adapterCalendar
    }

    private fun initData() {
        val appWidgetId = intent.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        if (appWidgetId == null || appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
        adapter.submitList(widgetBgColors.map { ContextCompat.getDrawable(this, it) })
        viewModel.getData(appWidgetId)
        viewModel.setupDays(Calendar.getInstance())
    }

    private fun setupEvents() = with(viewBinding) {
        buttonSave.setOnClickListener {
            viewModel.saveData(selectedColor, alpha, isWidgetLightContent(selectedColor))
            Handler(Looper.getMainLooper()).postDelayed({
                setupWidget()
            }, 200)
        }
        header.button1.setOnClickListener {
            finish()
        }
        adapter.onItemSelected = {
            selectedColor = widgetBgColors[it]
            updatePreview(isWidgetLightContent(selectedColor))
            adapter.selectedIndex = it
            adapter.notifyDataSetChanged()
        }
        seekbarAlpha.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                preview.viewBg.alpha = seekbarAlpha.progress / 100f
                alpha = seekbarAlpha.progress
                textSeekbarProgress.text = "${seekbarAlpha.progress}%"
            }
        })
    }

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenCreated {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                widgetModel.collect {
                    viewBinding.apply {
                        seekbarAlpha.progress = it.alpha
                        selectedColor = it.color
                        alpha = it.alpha
                        adapter.selectedIndex = widgetBgColors.indexOf(it.color)
                        adapter.notifyDataSetChanged()
                        preview.viewBg.alpha = alpha / 100f
                        seekbarAlpha.progress = alpha
                        textSeekbarProgress.text = "${seekbarAlpha.progress}%"
                        updatePreview(isWidgetLightContent(selectedColor))
                    }
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                days.collect {
                    if (it.isNotEmpty()) {
                        adapterCalendar.submitList(it)
                    }
                }
            }
        }
    }

    private fun updatePreview(isDark: Boolean) = with(viewBinding.preview) {
        viewBg.background =
            ContextCompat.getDrawable(
                this@MonthWidgetSettingActivity,
                widgetBgs[widgetBgColors.indexOf(selectedColor)]
            )
        val textTitleColor = ContextCompat.getColor(
            this@MonthWidgetSettingActivity,
            if (isDark) R.color.white else R.color.color_menu_text_default
        )
        textMonthYear.setTextColor(textTitleColor)
        imageNextMonth.setImageResource(R.drawable.ic_next)
        imagePreviousMonth.setImageResource(R.drawable.ic_previous)
        textDay1.setTextColor(textTitleColor)
        textDay2.setTextColor(textTitleColor)
        textDay3.setTextColor(textTitleColor)
        textDay4.setTextColor(textTitleColor)
        textDay5.setTextColor(textTitleColor)
        textDay6.setTextColor(textTitleColor)
        textDay7.setTextColor(textTitleColor)
        adapterCalendar.isDark = isDark
        adapterCalendar.notifyDataSetChanged()
        textMonthYear.text = DateTimeUtils.getMonthYearString(Calendar.getInstance())
    }

    private fun setupWidget() {
        var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                finish()
            }
            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(RESULT_OK, resultValue)
            finish()
            updateMonthWidget(
                applicationContext,
                appWidgetManager,
                appWidgetManager.getAppWidgetIds(
                    ComponentName(
                        application,
                        MonthWidget::class.java
                    )
                )
            )
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.gridCalendar)
        }
    }

    companion object {
        private const val MAX_NAME_LENGTH = 30
    }
}
