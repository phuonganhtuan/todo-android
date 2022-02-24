package com.trustedapp.todolist.planner.reminders.screens.home

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.common.chart.ChartColor
import com.trustedapp.todolist.planner.reminders.databinding.ActivityHomeBinding
import com.trustedapp.todolist.planner.reminders.screens.home.tasks.suggest.SuggestActivity
import com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.NotiReminderActivity
import com.trustedapp.todolist.planner.reminders.screens.theme.currentTheme
import com.trustedapp.todolist.planner.reminders.screens.theme.sceneryIds
import com.trustedapp.todolist.planner.reminders.screens.theme.textureIds
import com.trustedapp.todolist.planner.reminders.screens.widget.WidgetActivity
import com.trustedapp.todolist.planner.reminders.utils.SPUtils
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.show
import com.trustedapp.todolist.planner.reminders.widget.lite.LiteWidget
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
        setupTheme()
        viewBinding = inflateViewBinding()
        setContentView(viewBinding.root)
        onActivityReady()
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
        requestPermissions()
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
            layoutDrawer.close()
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

    private fun requestPermissions() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + packageName)
            )
            startActivityForResult(intent, 0)
        }
    }

    private fun onNavigationItemSelectedListener(item: MenuItem) {
        item.isChecked = true
        when (item.itemId) {
            R.id.navNotiReminder -> startActivity(Intent(this, NotiReminderActivity::class.java))
            R.id.navWidget -> startActivity(Intent(this, WidgetActivity::class.java))
        }
    }

    private fun updateWidget() {
        val components = listOf(
            StandardWidget::class.java,
            LiteWidget::class.java,
        )
        components.forEach {
            val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(
                ComponentName(application, it)
            )
            val appWidgetManager = AppWidgetManager.getInstance(this)
            appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.listTasks)
        }
    }
}
