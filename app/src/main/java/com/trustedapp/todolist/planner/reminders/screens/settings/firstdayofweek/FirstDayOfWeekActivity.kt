package com.trustedapp.todolist.planner.reminders.screens.settings.firstdayofweek

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
import com.trustedapp.todolist.planner.reminders.databinding.ActivityFirstDayOfWeekBinding
import com.trustedapp.todolist.planner.reminders.setting.FirstDayOfWeek
import com.trustedapp.todolist.planner.reminders.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class FirstDayOfWeekActivity : BaseActivity<ActivityFirstDayOfWeekBinding>() {

    override fun inflateViewBinding() = ActivityFirstDayOfWeekBinding.inflate(layoutInflater)

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
            textTitle.text = getString(R.string.first_day_of_week)
        }
        // Load Banner ads
//        loadBannerAds()
    }

    private fun loadBannerAds() = with(viewBinding) {
        if (Firebase.remoteConfig.getBoolean(SPUtils.KEY_BANNER) && isInternetAvailable()) {
            include.visibility = View.VISIBLE
            Admod.getInstance()
                .loadBanner(this@FirstDayOfWeekActivity, getString(R.string.banner_ads_id))
        } else {
            include.visibility = View.GONE
        }
    }

    private fun initData() {
        val firstDayOfWeek = SPUtils.getFirstDayOfWeek(this)
        val selectSettingIndex =
            listOf(
                FirstDayOfWeek.AUTO.name,
                FirstDayOfWeek.MONDAY.name,
                FirstDayOfWeek.SATURDAY.name,
                FirstDayOfWeek.SUNDAY.name,
            ).indexOf(firstDayOfWeek)
        viewBinding.radioGroup.check(viewBinding.radioGroup[selectSettingIndex].id)
    }

    private fun setupEvents() = with(viewBinding) {
        header.button1.setOnClickListener {
            finish()
        }
        radioButtonAuto.setOnClickListener {
            SPUtils.saveFirstDayOfWeek(
                this@FirstDayOfWeekActivity,
                FirstDayOfWeek.AUTO.name
            )
            updateWidget()
        }
        radioButtonMonday.setOnClickListener {
            SPUtils.saveFirstDayOfWeek(
                this@FirstDayOfWeekActivity,
                FirstDayOfWeek.MONDAY.name
            )
        }
        radioButtonSaturday.setOnClickListener {
            SPUtils.saveFirstDayOfWeek(
                this@FirstDayOfWeekActivity,
                FirstDayOfWeek.SATURDAY.name
            )
            updateWidget()
        }
        radioButtonSunday.setOnClickListener {
            SPUtils.saveFirstDayOfWeek(
                this@FirstDayOfWeekActivity,
                FirstDayOfWeek.SUNDAY.name
            )
            updateWidget()
        }
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
