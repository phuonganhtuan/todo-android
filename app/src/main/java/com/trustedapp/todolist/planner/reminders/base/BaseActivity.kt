package com.trustedapp.todolist.planner.reminders.base

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.screens.theme.currentTheme
import com.trustedapp.todolist.planner.reminders.utils.NetworkChangeReceiver
import com.trustedapp.todolist.planner.reminders.utils.SPUtils
import com.trustedapp.todolist.planner.reminders.widget.lite.LiteWidget
import com.trustedapp.todolist.planner.reminders.widget.month.MonthWidget
import com.trustedapp.todolist.planner.reminders.widget.month.updateMonthWidget
import com.trustedapp.todolist.planner.reminders.widget.standard.StandardWidget
import java.util.*


abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    private val networkReceiver = NetworkChangeReceiver()
    protected lateinit var viewBinding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        applyLanguage(SPUtils.getCurrentLang(this) ?: "en")
        if (currentTheme != -1) {
            setTheme(currentTheme)
        }
        viewBinding = inflateViewBinding()
        setContentView(viewBinding.root)
        applyLanguage(
            SPUtils.getCurrentLang(this) ?: "en"
        )
        onActivityReady(savedInstanceState)
        onActivityReady()
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(networkReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkReceiver)
    }

    abstract fun onActivityReady()

    abstract fun onActivityReady(savedInstanceState: Bundle?)

    abstract fun inflateViewBinding(): VB

    protected fun showToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    protected fun showSnackMessage(message: String) {
        Snackbar.make(viewBinding.root, message, Snackbar.LENGTH_SHORT)
    }

    protected fun isDarkMode() = resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES

    private fun makeStatusBarTransparent() {
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    protected fun applyLanguage(languageCode: String) {
        var langCode = languageCode
        if (langCode.isEmpty()) langCode = "en"
        val config = resources.configuration
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        config.setLocale(locale)
        createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
        SPUtils.saveCurrentLang(this, langCode)
        reload()
    }

    fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    fun updateWidget() {
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

    fun reload(){
        if (Build.VERSION.SDK_INT >= 11) {
            recreate()
        } else {
            val intent = intent
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }
}
