package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.ads.control.ads.Admod
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.databinding.ActivityNotiReminderBinding
import com.trustedapp.todolist.planner.reminders.utils.SPUtils
import com.trustedapp.todolist.planner.reminders.utils.isInternetAvailable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotiReminderActivity: BaseActivity<ActivityNotiReminderBinding>() {
    private val viewModel: NotiReminderViewModel by viewModels()

    override fun inflateViewBinding() = ActivityNotiReminderBinding.inflate(layoutInflater)
    override fun onActivityReady() {
        initView()
    }

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    private fun initView() = with(viewBinding) {
        // Load Banner ads
        if (Firebase.remoteConfig.getBoolean(SPUtils.KEY_BANNER) && isInternetAvailable()) {
            include.visibility = View.VISIBLE
            Admod.getInstance()
                .loadBanner(this@NotiReminderActivity, getString(R.string.banner_ads_id))
        } else {
            include.visibility = View.GONE
        }
    }


}