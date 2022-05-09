package com.trustedapp.todolist.planner.reminders.screens.home

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ads.control.ads.Admod
import com.ads.control.funtion.AdCallback
import com.bumptech.glide.Glide
import com.google.android.gms.ads.nativead.NativeAd
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.trustedapp.todolist.planner.reminders.BuildConfig
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.alarm.NotificationHelper
import com.trustedapp.todolist.planner.reminders.common.chart.ChartColor
import com.trustedapp.todolist.planner.reminders.databinding.ActivityHomeBinding
import com.trustedapp.todolist.planner.reminders.screens.home.exit.ExitDialogFragment
import com.trustedapp.todolist.planner.reminders.screens.home.tasks.suggest.SuggestActivity
import com.trustedapp.todolist.planner.reminders.screens.language.setting.LanguageSettingActivity
import com.trustedapp.todolist.planner.reminders.screens.settings.dateformat.DateFormatActivity
import com.trustedapp.todolist.planner.reminders.screens.settings.firstdayofweek.FirstDayOfWeekActivity
import com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.NotiReminderActivity
import com.trustedapp.todolist.planner.reminders.screens.settings.policy.PolicyActivity
import com.trustedapp.todolist.planner.reminders.screens.settings.rating.RatingDialogFragment
import com.trustedapp.todolist.planner.reminders.screens.settings.timeformat.TimeFormatActivity
import com.trustedapp.todolist.planner.reminders.screens.theme.ThemeSettingActivity
import com.trustedapp.todolist.planner.reminders.screens.theme.currentTheme
import com.trustedapp.todolist.planner.reminders.screens.theme.sceneryIds
import com.trustedapp.todolist.planner.reminders.screens.theme.textureIds
import com.trustedapp.todolist.planner.reminders.screens.widget.WidgetActivity
import com.trustedapp.todolist.planner.reminders.utils.*
import com.trustedapp.todolist.planner.reminders.utils.Constants.EXRA_APPEAR_RATE
import com.trustedapp.todolist.planner.reminders.utils.Constants.EXRA_LANGUAGE_UPDATED
import com.trustedapp.todolist.planner.reminders.utils.Constants.EXTRA_THEME_UPDATE
import com.trustedapp.todolist.planner.reminders.widget.lite.LiteWidget
import com.trustedapp.todolist.planner.reminders.widget.month.MonthWidget
import com.trustedapp.todolist.planner.reminders.widget.month.updateMonthWidget
import com.trustedapp.todolist.planner.reminders.widget.standard.StandardWidget
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    private fun inflateViewBinding() = ActivityHomeBinding.inflate(layoutInflater)

    private var texture = -1
    private var scenery = -1

    private val networkReceiver = NetworkChangeReceiver()
    var exitNativeAd: NativeAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyLanguage(SPUtils.getCurrentLang(this) ?: "en")
        setupTheme()
        viewBinding = inflateViewBinding()
        setContentView(viewBinding.root)
        onActivityReady()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val isAppearRate = intent?.extras?.getBoolean(EXRA_APPEAR_RATE)
        if (isAppearRate == true) {
            rateApp()
        }

        val isLanguageUpdated = intent?.extras?.getBoolean(EXRA_LANGUAGE_UPDATED)
        if (isLanguageUpdated == true) {
            recreate()
        }

        val isThemeUpdate = intent?.extras?.getBoolean(EXTRA_THEME_UPDATE)
        if (isThemeUpdate == true) {
            recreate()
        }
    }

    override fun onResume() {
        super.onResume()
        if (SPUtils.isFirstTime(this) && canShowSuggest) {
            viewModel.createInitData()
            SPUtils.saveFirstTimeLaunched(this)
            showSuggest()
        }
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(networkReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        updateWidget()
        canShowSuggest = true
        unregisterReceiver(networkReceiver)
    }

    private fun onActivityReady() {
        initViews()
        ChartColor.initChartColor(this)
        setupEvents()
        observeData()
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
//        loadBannerAds()
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
            R.id.navTheme -> ThemeSettingActivity::class.java
            R.id.navRateApp -> {
                rateApp()
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

    private fun rateApp() {
        if (!SPUtils.getIsRate(this)) {
            RatingDialogFragment().show(
                supportFragmentManager,
                RatingDialogFragment::class.java.simpleName
            )
        }
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
        intent.data = Uri.parse(getString(R.string.feedback_mail))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (isShowRateWhenExitApp()) {
            if (!SPUtils.getIsRate(this)) {
                val rateDialog = RatingDialogFragment()
                rateDialog.callBackWhenRate = {
                    finish()
                }
                rateDialog.show(
                    supportFragmentManager,
                    RatingDialogFragment::class.java.simpleName
                )
            }else{
                val exitDialog = ExitDialogFragment()
                exitDialog.exitNativeAd = exitNativeAd
                exitDialog.show(supportFragmentManager, ExitDialogFragment::class.java.simpleName)
            }
        } else {
            val exitDialog = ExitDialogFragment()
            exitDialog.exitNativeAd = exitNativeAd
            exitDialog.show(supportFragmentManager, ExitDialogFragment::class.java.simpleName)
        }
    }

    private fun isShowRateWhenExitApp() = try {
        val ratingExitNumber =
            Firebase.remoteConfig.getString(SPUtils.KEY_RATING_EXIT_NUMBER).split(",".toRegex())
        SPUtils.inCreaseNumberExit(this)
        val currentNumber = SPUtils.getExitNumber(this)
        ratingExitNumber.contains(currentNumber.toString())
    } catch (e: Exception) {
        false
    }

    private fun loadBannerAds() = with(viewBinding) {
        if (Firebase.remoteConfig.getBoolean(SPUtils.KEY_BANNER) && isInternetAvailable()) {
            include.findViewById<RelativeLayout>(R.id.ll_ads).findViewById<FrameLayout>(R.id.fl_shimemr).gone()
            include.visibility = View.VISIBLE
            Admod.getInstance()
                .loadBanner(this@HomeActivity, getString(R.string.banner_ads_id))
        } else {
            include.visibility = View.GONE
        }
    }

    private fun observeData() {
        lifecycleScope.launchWhenCreated {
            NetworkState.isHasInternet.collect {
                loadBannerAds()
                loadExitAds()
            }
        }
    }

    private fun loadExitAds() = with(viewBinding) {
        if (!FirebaseRemoteConfig.getInstance().getBoolean(SPUtils.KEY_NATIVE_EXIT)) {
            return@with
        }
        if (!isInternetAvailable()) return@with
        Admod.getInstance()
            .loadNativeAd(
                this@HomeActivity,
                getString(R.string.native_exit_ads_id),
                object : AdCallback() {
                    override fun onUnifiedNativeAdLoaded(unifiedNativeAd: NativeAd) {
                        super.onUnifiedNativeAdLoaded(unifiedNativeAd)
                        exitNativeAd = unifiedNativeAd
                    }
                })
    }
}


var canShowSuggest = false
