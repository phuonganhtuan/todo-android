package com.trustedapp.todolist.planner.reminders.base

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.screens.theme.currentTheme
import com.trustedapp.todolist.planner.reminders.widget.lite.LiteWidget
import com.trustedapp.todolist.planner.reminders.widget.standard.StandardWidget


abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var viewBinding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (currentTheme != -1) {
            setTheme(currentTheme)
        }
        viewBinding = inflateViewBinding()
        setContentView(viewBinding.root)
        onActivityReady()
        onActivityReady(savedInstanceState)
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

    fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    fun updateWidget() {
        val components = listOf(
            StandardWidget::class.java,
            LiteWidget::class.java,
        )
        components.forEach {
            val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(
                ComponentName(application, it)
            )
            val appWidgetManager = AppWidgetManager.getInstance(this)
            appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.listTasks)
        }
    }
}
