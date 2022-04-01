package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder

import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ads.control.ads.Admod
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.databinding.ActivityNotiReminderBinding
import com.trustedapp.todolist.planner.reminders.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class NotiReminderActivity: BaseActivity<ActivityNotiReminderBinding>() {
    private val viewModel: NotiReminderViewModel by viewModels()

    override fun inflateViewBinding() = ActivityNotiReminderBinding.inflate(layoutInflater)
    override fun onActivityReady() {
        initView()
        obseverData()
    }

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    private fun initView() = with(viewBinding) {
        // Load Banner ads
//        loadBannerAds()
    }

    private fun loadBannerAds() = with(viewBinding) {
        if (Firebase.remoteConfig.getBoolean(SPUtils.KEY_BANNER) && isInternetAvailable()) {
            include.visibility = View.VISIBLE
            Admod.getInstance()
                .loadBanner(this@NotiReminderActivity, getString(R.string.banner_ads_id))
        } else {
            include.visibility = View.GONE
        }
    }

    private fun initData() = with(viewModel){

    }

    fun obseverData(){
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                NetworkState.isHasInternet.collect {
//                    loadBannerAds()
                }
            }
        }
    }

}