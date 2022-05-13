package com.trustedapp.todolist.planner.reminders.screens.splash

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import com.ads.control.ads.Admod
import com.ads.control.funtion.AdCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.screens.home.HomeActivity
import com.trustedapp.todolist.planner.reminders.screens.language.start.LanguageSetupActivity
import com.trustedapp.todolist.planner.reminders.utils.SPUtils
import com.trustedapp.todolist.planner.reminders.utils.applyLanguage
import com.trustedapp.todolist.planner.reminders.utils.isInternetAvailable
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private var isLoadingAds: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyLanguage(SPUtils.getCurrentLang(this) ?: "en")
        getRemoteConfig()
        if (Build.VERSION.SDK_INT >= 31) {
            setTheme(R.style.Theme_App_Starting)
            val splashScreen = installSplashScreen()
            setContentView(R.layout.activity_splash)
            setSplashExitAnimation(splashScreen)
            setupTheme()
        } else {
            setTheme(R.style.Theme_AndroidBP_NoActionBar)
            setContentView(R.layout.activity_splash)
            setupTheme()
        }

        loadInterSplash()

    }

    private fun setupTheme() {
        try {
            val theme = SPUtils.getSavedTheme(this)
            val color = theme.first
            val themeId = when (color) {
                0 -> ContextCompat.getColor(this, R.color.color_theme_green)
                1 -> ContextCompat.getColor(this, R.color.color_red_theme)
                2 -> ContextCompat.getColor(this, R.color.color_orange_theme)
                3 -> ContextCompat.getColor(this, R.color.color_green_light_theme)
                4 -> ContextCompat.getColor(this, R.color.color_blue_theme)
                5 -> ContextCompat.getColor(this, R.color.color_purple_theme)
                else -> ContextCompat.getColor(this, R.color.color_theme_green)
            }
            val progress = findViewById<ProgressBar>(R.id.progressBar)
            progress.indeterminateTintList = ColorStateList.valueOf(themeId)
            val tvAds = findViewById<TextView>(R.id.tvTextAds)
            tvAds.setTextColor(themeId)
        } catch (ex: Exception) {
            val progress = findViewById<ProgressBar>(R.id.progressBar)
            progress.indeterminateTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_theme_green))
            val tvAds = findViewById<TextView>(R.id.tvTextAds)
            tvAds.setTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.color_theme_green
                    )
                )
            )
        }

    }

    private fun toHomeDelayed() {
        if (SPUtils.isFirstTime(this)) {
            startActivity(Intent(this, LanguageSetupActivity::class.java))
        } else {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        finish()
    }

    private fun setSplashExitAnimation(splashScreen: SplashScreen) {
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            configureObjectAnimator(splashScreenView) { slideUpAnimation ->
                with(slideUpAnimation) {
                    interpolator = AnticipateInterpolator()
                    duration = 600L
                    doOnEnd {
                        splashScreenView.remove()
                    }
                    start()
                }
            }
        }
    }

    private fun getRemoteConfig() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
            fetchTimeoutInSeconds = 5
        }
        Firebase.remoteConfig.setConfigSettingsAsync(configSettings)
        Firebase.remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        loadConfig()

    }

    private fun loadConfig() {
        Firebase.remoteConfig.fetchAndActivate()

    }

    private fun configureObjectAnimator(
        splashScreenView: SplashScreenViewProvider,
        onComplete: (ObjectAnimator) -> Unit
    ) {
        val objectAnimator = ObjectAnimator.ofFloat(
            splashScreenView.view,
            View.TRANSLATION_Y,
            0f,
            -400f
        )
        onComplete.invoke(objectAnimator)
    }

    private fun loadInterSplash() {

        object : CountDownTimer(3000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                if (!Firebase.remoteConfig.getBoolean(SPUtils.KEY_INTER_SPLASH)) {
                    toHomeDelayed()
                    return
                }

                if (!isInternetAvailable()) {
                    isLoadingAds = false
                    toHomeDelayed()
                    return
                }

                isLoadingAds = true

                Admod.getInstance().getInterstitalAds(
                    this@SplashActivity,
                    getString(R.string.inter_splash_ads_id),
                    object : AdCallback() {
                        override fun onInterstitialLoad(interstitialAd: InterstitialAd) {
                            Admod.getInstance().setOpenActivityAfterShowInterAds(true)
                            Admod.getInstance()
                                .forceShowInterstitial(
                                    this@SplashActivity,
                                    interstitialAd,
                                    object : AdCallback() {
                                        override fun onAdClosed() {
                                            isLoadingAds = false
                                            toHomeDelayed()
                                        }
                                    })
                        }

                        override fun onAdClosed() {
                            super.onAdClosed()
                            isLoadingAds = false
                            toHomeDelayed()
                        }

                        override fun onAdLeftApplication() {
                            super.onAdLeftApplication()
                            isLoadingAds = false
                            toHomeDelayed()
                        }

                        override fun onAdFailedToLoad(i: LoadAdError?) {
                            super.onAdFailedToLoad(i)
                            isLoadingAds = false
                            toHomeDelayed()
                        }
                    }
                )
            }
        }.start()
    }
}
