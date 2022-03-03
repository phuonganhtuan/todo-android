package com.trustedapp.todolist.planner.reminders.widget.lite

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.SeekBar
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.data.datasource.local.database.AppDatabase
import com.trustedapp.todolist.planner.reminders.data.datasource.local.impl.TaskLocalDataSourceImpl
import com.trustedapp.todolist.planner.reminders.data.datasource.local.impl.WidgetDataSourceImpl
import com.trustedapp.todolist.planner.reminders.data.models.entity.CategoryEntity
import com.trustedapp.todolist.planner.reminders.data.models.entity.FilterTime
import com.trustedapp.todolist.planner.reminders.data.repository.impl.TaskRepositoryImpl
import com.trustedapp.todolist.planner.reminders.data.repository.impl.WidgetRepositoryImpl
import com.trustedapp.todolist.planner.reminders.databinding.ActivityWidgetLiteSettingBinding
import com.trustedapp.todolist.planner.reminders.screens.newtask.category.OnCatInteractListener
import com.trustedapp.todolist.planner.reminders.screens.newtask.category.SelectCategoryAdapter
import com.trustedapp.todolist.planner.reminders.screens.theme.ThemeTextureColorAdapter
import com.trustedapp.todolist.planner.reminders.utils.getStringByLocale
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.helper.getCatName
import com.trustedapp.todolist.planner.reminders.utils.hide
import com.trustedapp.todolist.planner.reminders.widget.isWidgetLightContent
import com.trustedapp.todolist.planner.reminders.widget.widgetBgColors
import com.trustedapp.todolist.planner.reminders.widget.widgetBgsRoundTop
import kotlinx.coroutines.flow.collect

class LiteWidgetSettingActivity : BaseActivity<ActivityWidgetLiteSettingBinding>() {

    private val viewModel by lazy {
        LiteWidgetSettingViewModel(
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

    private val taskPreviewAdapter = LitePreviewWidgetAdapter()

    private var categoriesPopup: PopupWindow? = null

    private val categoryAdapter = SelectCategoryAdapter()

    private var selectCatIndex = 0

    private var selectCatId: Int? = null
    private var isOnlyToday = true

    private var timeFilter = FilterTime.TODAY.name

    override fun inflateViewBinding() =
        ActivityWidgetLiteSettingBinding.inflate(layoutInflater)

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
            textTitle.text = getString(R.string.lite)
        }
        recyclerColors.adapter = adapter
        recyclerTasks.adapter = taskPreviewAdapter
        preview.textEmptyWidget.gone()
        preview.imageAddTask.setBackgroundResource(R.drawable.ic_add_task_widget)
        preview.imageSetting.setBackgroundResource(R.drawable.ic_setting)
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
    }

    private fun setupEvents() = with(viewBinding) {
        buttonSave.setOnClickListener {
            viewModel.saveData(
                selectedColor,
                alpha,
                isWidgetLightContent(selectedColor),
                timeFilter == FilterTime.TODAY.name,
                if (selectCatIndex == 0) null else viewModel.categories.value[selectCatIndex - 1].id,
                switchCompleted.isChecked,
            )
            Handler(Looper.getMainLooper()).postDelayed({
                setupWidget()
            }, 200)
        }
        header.button1.setOnClickListener {
            finish()
        }
        buttonCat.setOnClickListener {
            categoriesPopup?.showAsDropDown(it, -50, -80)
        }
        buttonTime.setOnClickListener {
            showTimeFilter()
        }
        switchCompleted.setOnClickListener {
            viewModel.getPreviewData(
                this@LiteWidgetSettingActivity,
                switchCompleted.isChecked,
                selectCatId,
                isOnlyToday,
            )
        }
        categoryAdapter.setOnCatListener(object : OnCatInteractListener {
            override fun onCatClick(index: Int) {
                selectCatIndex = index
                val catName = if (index == 0) getString(R.string.all_categories) else getCatName(
                    this@LiteWidgetSettingActivity,
                    viewModel.categories.value[index - 1].name
                )
                selectCatId = if (index == 0) null else viewModel.categories.value[index - 1].id
                buttonCat.text = catName
                categoriesPopup?.dismiss()
                viewModel.getPreviewData(
                    this@LiteWidgetSettingActivity,
                    switchCompleted.isChecked,
                    selectCatId,
                    isOnlyToday
                )
            }
        })
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
                preview.viewBgTitle.alpha = seekbarAlpha.progress / 100f
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
                        preview.viewBgTitle.alpha = alpha / 100f
                        textSeekbarProgress.text = "${seekbarAlpha.progress}%"

                        Handler(Looper.getMainLooper()).postDelayed({
                            buttonCat.text =
                                if (it.categoryId == null) getString(R.string.all_categories) else getCatName(
                                    this@LiteWidgetSettingActivity,
                                    viewModel.categories.value.firstOrNull { item -> item.id == it.categoryId }?.name
                                        ?: ""
                                )
                        }, 300)

                        buttonTime.text =
                            if (it.isOnlyToday) getString(R.string.today) else getString(R.string.all_time)

                        switchCompleted.isChecked = it.containCompleted
                        isOnlyToday = it.isOnlyToday
                        selectCatId = it.categoryId
                        getPreviewData(
                            this@LiteWidgetSettingActivity,
                            it.containCompleted,
                            it.categoryId,
                            it.isOnlyToday
                        )

                        updatePreview(isWidgetLightContent(selectedColor))
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                categories.collect {
                    val catIdSum = it.sumOf { cat -> cat.id }
                    val listCats = it.toMutableList().apply {
                        add(
                            0,
                            CategoryEntity(
                                catIdSum,
                                name = getString(R.string.all_categories)
                            )
                        )
                    }
                    categoryAdapter.submitList(listCats)
                    setupCatsPopup(listCats)
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                tasks.collect {
                    taskPreviewAdapter.submitList(it)
                    val titlePrefix =
                        if (isOnlyToday) getStringByLocale(R.string.today) else getStringByLocale(
                            R.string.tasks
                        )
                    var titleCat = ""
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (selectCatId != null && selectCatIndex > 0) {
                            titleCat = " (${
                                getCatName(
                                    this@LiteWidgetSettingActivity,
                                    viewModel.categories.value[selectCatIndex - 1].name
                                )
                            })"
                        }
                        viewBinding.preview.textTitleWidget.text = titlePrefix + titleCat
                    }, 500)
                }
            }
        }
    }

    private fun updatePreview(isDark: Boolean) = with(viewBinding.preview) {
        viewBgTitle.background =
            ContextCompat.getDrawable(
                this@LiteWidgetSettingActivity,
                widgetBgsRoundTop[widgetBgColors.indexOf(selectedColor)]
            )
        val textTitleColor = ContextCompat.getColor(
            this@LiteWidgetSettingActivity,
            if (isDark) R.color.white else R.color.color_menu_text_default
        )
        textTitleWidget.setTextColor(textTitleColor)
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
            updateLiteWidget(
                applicationContext,
                appWidgetManager,
                appWidgetId
            )
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listTasks)
        }
    }

    private fun showTimeFilter() {
        val popup = PopupMenu(this, viewBinding.buttonTime)
        popup.menuInflater.inflate(R.menu.widget_time_filter_menu, popup.menu)
        val item = popup.menu.getItem(0)
        val item1 = popup.menu.getItem(1)
        item.title = getStringByLocale(R.string.all_time)
        item1.title = getStringByLocale(R.string.today)

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.allTasks -> {
                    timeFilter = FilterTime.ALL_TIME.name
                    viewBinding.buttonTime.text = getString(R.string.all_time)
                    isOnlyToday = false
                    viewModel.getPreviewData(
                        this@LiteWidgetSettingActivity,
                        viewBinding.switchCompleted.isChecked,
                        selectCatId,
                        isOnlyToday,
                    )
                }
                R.id.today -> {
                    timeFilter = FilterTime.TODAY.name
                    viewBinding.buttonTime.text = getString(R.string.today)
                    isOnlyToday = true
                    viewModel.getPreviewData(
                        this@LiteWidgetSettingActivity,
                        viewBinding.switchCompleted.isChecked,
                        selectCatId,
                        isOnlyToday,
                    )
                }
            }
            true
        }
        popup.show()
    }

    private fun setupCatsPopup(cats: List<CategoryEntity>) {
        val inflater = this.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.layout_select_category, null)
        popupView.elevation = 12f

        val recyclerCats = popupView.findViewById<RecyclerView>(R.id.recyclerCategory)
        recyclerCats.adapter = categoryAdapter
        categoriesPopup =
            PopupWindow(
                popupView,
                480,
                if (cats.size <= 5) ViewGroup.LayoutParams.WRAP_CONTENT else 600,
                true
            )
    }

    companion object {
        private const val MAX_NAME_LENGTH = 30
    }
}
