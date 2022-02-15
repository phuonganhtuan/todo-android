package com.trustedapp.todolist.planner.reminders.screens.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.common.chart.ChartColor
import com.trustedapp.todolist.planner.reminders.databinding.ActivityHomeBinding
import com.trustedapp.todolist.planner.reminders.screens.home.tasks.suggest.SuggestActivity
import com.trustedapp.todolist.planner.reminders.screens.theme.currentTheme
import com.trustedapp.todolist.planner.reminders.utils.SPUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    private fun inflateViewBinding() = ActivityHomeBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTheme()
        viewBinding = inflateViewBinding()
        setContentView(viewBinding.root)
        onActivityReady()
    }


    private fun onActivityReady() {

        if (SPUtils.isFirstTime(this)) {
            viewModel.createInitData()
            SPUtils.saveFirstTimeLaunched(this)
            showSuggest()
        }
        ChartColor.initChartColor(this)
        setupEvents()
        requestPermissions()
    }

    fun openDrawer() {
        viewBinding.layoutDrawer.open()
    }

    private fun showSuggest() {
        startActivity(Intent(this, SuggestActivity::class.java))
    }

    private fun setupEvents() = with(viewBinding) {
        bottomBar.setupWithNavController(findNavController(R.id.home_nav_host_fragment))
//        (navigationSideView.getHeaderView(0)
//            .findViewById(R.id.buttonHide) as ImageView).setOnClickListener {
//            layoutDrawer.close()
//        }
    }

    private fun setupTheme() {
        val theme = SPUtils.getSavedTheme(this)
        val color = theme.first
        val texture = theme.second
        val scenery = theme.third
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
}
