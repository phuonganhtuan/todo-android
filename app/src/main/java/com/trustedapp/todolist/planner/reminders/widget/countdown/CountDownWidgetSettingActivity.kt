package com.trustedapp.todolist.planner.reminders.widget.countdown

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.data.datasource.local.database.AppDatabase
import com.trustedapp.todolist.planner.reminders.data.datasource.local.impl.WidgetDataSourceImpl
import com.trustedapp.todolist.planner.reminders.data.models.entity.CountDownType
import com.trustedapp.todolist.planner.reminders.data.repository.impl.WidgetRepositoryImpl
import com.trustedapp.todolist.planner.reminders.databinding.ActivityWidgetCountDownSettingBinding
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.hide
import com.trustedapp.todolist.planner.reminders.utils.show
import kotlinx.coroutines.flow.collect
import java.util.*

class CountDownWidgetSettingActivity : BaseActivity<ActivityWidgetCountDownSettingBinding>() {

    private val viewModel by lazy {
        CountDownWidgetSettingViewModel(
            WidgetRepositoryImpl(
                WidgetDataSourceImpl(AppDatabase.invoke(this).taskDao())
            )
        )
    }

    private var type = CountDownType.REMAIN_DAYS.name

    private var selectedDate = System.currentTimeMillis()

    private val datePicker = MaterialDatePicker.Builder.datePicker().build()

    private var isShowingIconList = false

    private var iconIndex = 0

    override fun inflateViewBinding() =
        ActivityWidgetCountDownSettingBinding.inflate(layoutInflater)

    override fun onActivityReady(savedInstanceState: Bundle?) {
        setResult(RESULT_CANCELED)
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
            textTitle.text = getString(R.string.count_down)
        }
        textCharCount.text = "0/$MAX_NAME_LENGTH"
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
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultValue)
        viewModel.getData(appWidgetId)
        viewBinding.apply {
            val iconButtonList = listOf(icon1, icon2, icon3, icon4, icon5, icon6, icon7, icon8)
            for (i in iconButtonList.indices) {
                Glide.with(this@CountDownWidgetSettingActivity).load(countDownEventIconIds[i])
                    .into(iconButtonList[i])
            }
        }
    }

    private fun setupEvents() = with(viewBinding) {
        buttonCountType.setOnClickListener {
            showMoreMenu()
        }
        buttonDay.setOnClickListener {
            selectDay()
        }
        datePicker.addOnPositiveButtonClickListener {
            selectedDate = it
            buttonDay.text = DateTimeUtils.getComparableDateString(
                Calendar.getInstance().apply { timeInMillis = it }.time
            )
        }
        buttonSave.setOnClickListener {
            if (editEventName.text.isNullOrEmpty()) {
                showToastMessage(getString(R.string.please_set_event_name))
                return@setOnClickListener
            }
            viewModel.saveData(editEventName.text.toString(), selectedDate, iconIndex, type)
            Handler(Looper.getMainLooper()).postDelayed({
                setupWidget()
            }, 200)
        }
        header.button1.setOnClickListener {
            finish()
        }
        imageIcon.setOnClickListener {
            isShowingIconList = !isShowingIconList
            if (isShowingIconList) layoutIcons.show() else layoutIcons.gone()
        }
        layoutIcons.setOnClickListener {
        }
        icon1.setOnClickListener {
            selectIcon(0)
        }
        icon2.setOnClickListener {
            selectIcon(1)
        }
        icon3.setOnClickListener {
            selectIcon(2)
        }
        icon4.setOnClickListener {
            selectIcon(3)
        }
        icon5.setOnClickListener {
            selectIcon(4)
        }
        icon6.setOnClickListener {
            selectIcon(5)
        }
        icon7.setOnClickListener {
            selectIcon(6)
        }
        icon8.setOnClickListener {
            selectIcon(7)
        }
        editEventName.addTextChangedListener(object : TextWatcher {

            var currentName = ""
            var currentCursor = 0


            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                currentName = editEventName.text.toString()
                currentCursor = editEventName.selectionStart
            }

            override fun afterTextChanged(p0: Editable?) {
                if (editEventName.text.toString().length > MAX_NAME_LENGTH) {
                    editEventName.setText(currentName)
                    editEventName.setSelection(currentCursor - 1)
                }
                textCharCount.text = "${editEventName.text.toString().length}/$MAX_NAME_LENGTH"
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    private fun selectIcon(index: Int) = with(viewBinding) {
        iconIndex = index
        imageIcon.setImageResource(countDownEventIconIds[index])
        isShowingIconList = false
        layoutIcons.gone()
    }

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenCreated {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                widgetModel.collect {
                    viewBinding.apply {
                        editEventName.setText(it.eventName)
                        buttonDay.text = DateTimeUtils.getComparableDateString(
                            Calendar.getInstance().apply { timeInMillis = it.date }.time
                        )
                        buttonCountType.text = getString(
                            when (it.countType) {
                                CountDownType.DAYS.name -> R.string.days
                                else -> R.string.remaining_days
                            }
                        )
                        type = it.countType
                        selectedDate = it.date
                        iconIndex = it.iconIndex
                        imageIcon.setImageResource(countDownEventIconIds[iconIndex])
                    }
                }
            }
        }
    }

    private fun showMoreMenu() {
        val popup = PopupMenu(this, viewBinding.buttonCountType)
        popup.menuInflater.inflate(R.menu.count_type_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.remainDays -> {
                    type = CountDownType.REMAIN_DAYS.name
                    viewBinding.buttonCountType.text = getString(R.string.remaining_days)
                }
                R.id.days -> {
                    type = CountDownType.DAYS.name
                    viewBinding.buttonCountType.text = getString(R.string.days)
                }
            }
            true
        }
        popup.show()
    }

    private fun selectDay() {
        datePicker.show(supportFragmentManager, datePicker.toString())
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
            updateCountDownWidget(
                applicationContext, appWidgetManager, appWidgetId
            )
        }
    }

    companion object {
        private const val MAX_NAME_LENGTH = 30
    }
}
