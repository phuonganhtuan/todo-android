package com.trustedapp.todolist.planner.reminders.screens.splash

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.screens.home.HomeActivity
import com.trustedapp.todolist.planner.reminders.utils.SPUtils
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var remoteConfig: FirebaseRemoteConfig
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
        toHomeDelayed()
    }

    private fun toHomeDelayed() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }, 1500L)
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

    private fun getRemoteConfig(){
        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        loadConfig()
    }

    private fun loadConfig(){
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.e("loadConfig", "Config params updated: $updated")
                    Log.e("loadConfig", "Config params updated: $remoteConfig")
                } else {
                    Log.e("loadConfig", "Config params updated - false")
                }
                parserRemoteConfig(remoteConfig)
            }
    }

    private fun parserRemoteConfig(config: FirebaseRemoteConfig){
        config.let {
            SPUtils.setRemoteConfig(this, SPUtils.KEY_INTER_SPLASH, config.getBoolean(SPUtils.KEY_INTER_SPLASH))
            SPUtils.setRemoteConfig(this, SPUtils.KEY_BANNER, config.getBoolean(SPUtils.KEY_INTER_SPLASH))
            SPUtils.setRemoteConfig(this, SPUtils.KEY_INTER_INSERT, config.getBoolean(SPUtils.KEY_INTER_SPLASH))
            SPUtils.setRemoteConfig(this, SPUtils.KEY_NATIVE_LANGUAGE, config.getBoolean(SPUtils.KEY_INTER_SPLASH))
            SPUtils.setRemoteConfig(this, SPUtils.KEY_INTER_THEME, config.getBoolean(SPUtils.KEY_INTER_SPLASH))
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
}
