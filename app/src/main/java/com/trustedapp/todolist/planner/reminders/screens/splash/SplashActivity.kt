package com.trustedapp.todolist.planner.reminders.screens.splash

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import com.ads.control.ads.Admod
import com.ads.control.funtion.AdCallback
import com.trustedapp.todolist.planner.reminders.screens.language.start.LanguageSetupActivity
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.screens.home.HomeActivity
import com.trustedapp.todolist.planner.reminders.utils.SPUtils
import com.trustedapp.todolist.planner.reminders.utils.isInternetAvailable
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private var isLoadingAds: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 31) {
            setTheme(R.style.Theme_App_Starting)
            val splashScreen = installSplashScreen()
            setContentView(R.layout.activity_splash)
            setSplashExitAnimation(splashScreen)
        } else {
            setTheme(R.style.Theme_AndroidBP_NoActionBar)
            setContentView(R.layout.activity_splash)
        }
        getRemoteConfig()

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
        }
        Firebase.remoteConfig.setConfigSettingsAsync(configSettings)
        Firebase.remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        loadConfig()

    }

    private fun loadConfig() {
        Firebase.remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.e("loadConfig", "Config params updated: $updated")
                    Log.e("loadConfig", "Config params updated: $Firebase.remoteConfig")
                } else {
                    Log.e("loadConfig", "Config params updated - false")
                }
                loadInterSplash()
            }
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
}
