package com.trustedapp.todolist.planner.reminders.screens.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.common.chart.ChartColor
import com.trustedapp.todolist.planner.reminders.databinding.ActivityHomeBinding
import com.trustedapp.todolist.planner.reminders.screens.home.tasks.suggest.SuggestActivity
import com.trustedapp.todolist.planner.reminders.utils.SPUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    private val viewModel: HomeViewModel by viewModels()



    override fun inflateViewBinding() = ActivityHomeBinding.inflate(layoutInflater)

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {

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
