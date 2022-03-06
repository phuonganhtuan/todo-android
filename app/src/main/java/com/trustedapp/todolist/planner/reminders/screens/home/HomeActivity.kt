package com.trustedapp.todolist.planner.reminders.screens.home

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.trustedapp.todolist.planner.reminders.BuildConfig
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.alarm.NotificationHelper
import com.trustedapp.todolist.planner.reminders.common.chart.ChartColor
import com.trustedapp.todolist.planner.reminders.databinding.ActivityHomeBinding
import com.trustedapp.todolist.planner.reminders.screens.home.tasks.suggest.SuggestActivity
import com.trustedapp.todolist.planner.reminders.screens.language.setting.LanguageSettingActivity
import com.trustedapp.todolist.planner.reminders.screens.settings.dateformat.DateFormatActivity
import com.trustedapp.todolist.planner.reminders.screens.settings.firstdayofweek.FirstDayOfWeekActivity
import com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.NotiReminderActivity
import com.trustedapp.todolist.planner.reminders.screens.settings.policy.PolicyActivity
import com.trustedapp.todolist.planner.reminders.screens.settings.rating.RatingDialogFragment
import com.trustedapp.todolist.planner.reminders.screens.settings.timeformat.TimeFormatActivity
import com.trustedapp.todolist.planner.reminders.screens.theme.currentTheme
import com.trustedapp.todolist.planner.reminders.screens.theme.sceneryIds
import com.trustedapp.todolist.planner.reminders.screens.theme.textureIds
import com.trustedapp.todolist.planner.reminders.screens.widget.WidgetActivity
import com.trustedapp.todolist.planner.reminders.utils.Constants.EXRA_LANGUAGE_UPDATED
import com.trustedapp.todolist.planner.reminders.utils.SPUtils
import com.trustedapp.todolist.planner.reminders.utils.applyLanguage
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.show
import com.trustedapp.todolist.planner.reminders.widget.lite.LiteWidget
import com.trustedapp.todolist.planner.reminders.widget.month.MonthWidget
import com.trustedapp.todolist.planner.reminders.widget.month.updateMonthWidget
import com.trustedapp.todolist.planner.reminders.widget.standard.StandardWidget
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    private fun inflateViewBinding() = ActivityHomeBinding.inflate(layoutInflater)

    private var texture = -1
    private var scenery = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        )
        applyLanguage(SPUtils.getCurrentLang(this) ?: "en")
        setupTheme()
        viewBinding = inflateViewBinding()
        setContentView(viewBinding.root)
        onActivityReady()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val isLanguageUpdated = intent?.extras?.getBoolean(EXRA_LANGUAGE_UPDATED)
        if (isLanguageUpdated == true) {
            recreate()
        }
    }

    override fun onPause() {
        super.onPause()
        updateWidget()
    }

    private fun onActivityReady() {
        initViews()
        if (SPUtils.isFirstTime(this)) {
            viewModel.createInitData()
            SPUtils.saveFirstTimeLaunched(this)
            showSuggest()
        }
        ChartColor.initChartColor(this)
        setupEvents()
    }

    private fun initViews() = viewBinding.contentHome.apply {
        imageOverlay.gone()
        if (scenery > -1) {
            imageOverlay.show()
            Glide.with(this@HomeActivity)
                .load(ContextCompat.getDrawable(this@HomeActivity, sceneryIds[scenery]))
                .into(imageBg)
            return@apply
        }
        if (texture > -1) {
            imageOverlay.gone()
            Glide.with(this@HomeActivity)
                .load(ContextCompat.getDrawable(this@HomeActivity, textureIds[texture]))
                .into(imageBg)
        }
        val versionItem: MenuItem = viewBinding.navigationSideView.menu.findItem(R.id.navVersion)
        versionItem.title = Html.fromHtml(
            String.format(
                getString(R.string.version_info),
                BuildConfig.VERSION_NAME
            ), FROM_HTML_MODE_LEGACY
        )
        if (SPUtils.getIsAddTaskFromNotificationBar(this@HomeActivity)) NotificationHelper.createAddTaskNotification(
            this@HomeActivity
        )
    }

    fun openDrawer() {
        viewBinding.layoutDrawer.open()
    }

    private fun showSuggest() {
        startActivity(Intent(this, SuggestActivity::class.java))
    }

    private fun setupEvents() = with(viewBinding) {
        bottomBar.setupWithNavController(findNavController(R.id.home_nav_host_fragment))
        (navigationSideView.getHeaderView(0)
            .findViewById(R.id.buttonHide) as ImageView).setOnClickListener {
            layoutDrawer.close()
        }
        navigationSideView.setNavigationItemSelectedListener {
            onNavigationItemSelectedListener(it)
//            layoutDrawer.close()
            true
        }
    }

    private fun setupTheme() {
        val theme = SPUtils.getSavedTheme(this)
        val color = theme.first
        texture = theme.second
        scenery = theme.third
        val themeId = when (color) {
            0 -> R.style.Theme_AndroidBP_Green
            1 -> R.style.Theme_AndroidBP_Red
            2 -> R.style.Theme_AndroidBP_Orange
            3 -> R.style.Theme_AndroidBP_GreenLight
            4 -> R.style.Theme_AndroidBP_Blue
            5 -> R.style.Theme_AndroidBP_Purple
            else -> R.style.Theme_AndroidBP_Green
        }
        currentTheme = themeId
        setTheme(themeId)
    }

    private fun onNavigationItemSelectedListener(item: MenuItem) {
        item.isChecked = true
        val activity = when (item.itemId) {
            R.id.navNotiReminder -> NotiReminderActivity::class.java
            R.id.navWidget -> WidgetActivity::class.java
            R.id.navFirstdayOfWeek -> FirstDayOfWeekActivity::class.java
            R.id.navTimeFormat -> TimeFormatActivity::class.java
            R.id.navDateFormat -> DateFormatActivity::class.java
            R.id.navLanguage -> LanguageSettingActivity::class.java
            R.id.navPolicy -> PolicyActivity::class.java
            R.id.navRateApp -> {
                RatingDialogFragment().show(
                    supportFragmentManager,
                    RatingDialogFragment::class.java.simpleName
                )
                return
            }
            R.id.navShareApp -> {
                shareApp()
                return
            }
            R.id.navFeedback -> {
                sendFeedback()
                return
            }
            else -> return
        }
        startActivity(Intent(this, activity))
    }

    private fun updateWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)

        val components = listOf(
            StandardWidget::class.java,
            LiteWidget::class.java,
        )
        components.forEach {
            val ids = appWidgetManager.getAppWidgetIds(
                ComponentName(application, it)
            )
            appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.listTasks)
        }
        updateMonthWidget(
            this, appWidgetManager, appWidgetManager.getAppWidgetIds(
                ComponentName(
                    application,
                    MonthWidget::class.java
                )
            )
        )
        appWidgetManager.apply {
            notifyAppWidgetViewDataChanged(
                getAppWidgetIds(
                    ComponentName(
                        application,
                        MonthWidget::class.java
                    )
                ), R.id.gridCalendar
            )
        }
    }

    private fun shareApp() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "http://play.google.com/store/apps/details?id=$packageName"
        )
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
    }

    private fun sendFeedback() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject))
        intent.data = Uri.parse("mailto:demomail@gmail.com")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
