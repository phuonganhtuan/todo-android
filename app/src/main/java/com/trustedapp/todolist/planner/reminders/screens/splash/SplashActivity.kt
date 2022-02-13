package com.trustedapp.todolist.planner.reminders.screens.splash

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.screens.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

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
