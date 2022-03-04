package com.trustedapp.todolist.planner.reminders.screens.settings.timeformat

import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.core.view.get
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ads.control.ads.Admod
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.databinding.ActivityTimeFormatBinding
import com.trustedapp.todolist.planner.reminders.setting.DefaultSetting
import com.trustedapp.todolist.planner.reminders.setting.TimeFormat
import com.trustedapp.todolist.planner.reminders.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class TimeFormatActivity : BaseActivity<ActivityTimeFormatBinding>() {


    override fun inflateViewBinding() = ActivityTimeFormatBinding.inflate(layoutInflater)

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {
        initViews()
        initData()
        setupEvents()
        obseverData()
    }

    private fun initViews() = with(viewBinding) {
        header.apply {
            button1.setImageResource(R.drawable.ic_arrow_left)
            button2.hide()
            button3.hide()
            button4.hide()
            textTitle.text = getString(R.string.time_format)
        }
        // Load Banner ads
        loadBannerAds()
    }

    private fun loadBannerAds() = with(viewBinding) {
        if (Firebase.remoteConfig.getBoolean(SPUtils.KEY_BANNER) && isInternetAvailable()) {
            include.visibility = View.VISIBLE
            Admod.getInstance()
                .loadBanner(this@TimeFormatActivity, getString(R.string.banner_ads_id))
        } else {
            include.visibility = View.GONE
        }
    }

    private fun initData() {
        val format = SPUtils.getTimeFormat(this)
        val selectSettingIndex =
            listOf(
                TimeFormat.DEFAULT.name,
                TimeFormat.H24.name,
                TimeFormat.H12.name
            ).indexOf(format)
        viewBinding.radioGroup.check(viewBinding.radioGroup[selectSettingIndex].id)
    }

    private fun setupEvents() = with(viewBinding) {
        header.button1.setOnClickListener {
            finish()
        }
        radioButtonDefault.setOnClickListener {
            SPUtils.saveTimeFormat(
                this@TimeFormatActivity,
                TimeFormat.DEFAULT.name
            )
            updateWidget()
        }
        radioButton12.setOnClickListener {
            SPUtils.saveTimeFormat(
                this@TimeFormatActivity,
                TimeFormat.H12.name
            )
            updateWidget()
        }
        radioButton24.setOnClickListener {
            SPUtils.saveTimeFormat(
                this@TimeFormatActivity,
                TimeFormat.H24.name
            )
            updateWidget()
        }
    }

    fun obseverData(){
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                NetworkState.isHasInternet.collect {
                    viewBinding.include.apply {
                        loadBannerAds()
                    }
                }
            }
        }
    }
}
