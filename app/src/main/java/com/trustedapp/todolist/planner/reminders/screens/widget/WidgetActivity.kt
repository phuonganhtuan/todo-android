package com.trustedapp.todolist.planner.reminders.screens.widget

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.bumptech.glide.Glide
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.databinding.ActivityWidgetBinding
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.widget.countdown.CountdownWidget
import com.trustedapp.todolist.planner.reminders.widget.lite.LiteWidget
import com.trustedapp.todolist.planner.reminders.widget.month.MonthWidget
import com.trustedapp.todolist.planner.reminders.widget.standard.StandardWidget
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WidgetActivity : BaseActivity<ActivityWidgetBinding>() {

    override fun inflateViewBinding() = ActivityWidgetBinding.inflate(layoutInflater)

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {
        initViews()
        setupEvents()
    }

    private fun initViews() = with(viewBinding) {
        header.apply {
            button2.gone()
            button3.gone()
            button4.gone()
            button1.setImageResource(R.drawable.ic_arrow_left)
            textTitle.text = getString(R.string.widget)
        }
        w1.apply {
            textWName.text = getString(R.string.standard)
            textWSize.text = getString(R.string.size44)
            Glide.with(this@WidgetActivity).load(R.drawable.img_w_1).into(image1)
            Glide.with(this@WidgetActivity).load(R.drawable.img_w_2).into(image2)
        }
        w2.apply {
            textWName.text = getString(R.string.lite)
            textWSize.text = getString(R.string.size32)
            Glide.with(this@WidgetActivity).load(R.drawable.img_w_3).into(image1)
            Glide.with(this@WidgetActivity).load(R.drawable.img_w_4).into(image2)
        }
        w3.apply {
            textWName.text = getString(R.string.month)
            textWSize.text = getString(R.string.size43)
            Glide.with(this@WidgetActivity).load(R.drawable.img_w_5).into(image1)
            Glide.with(this@WidgetActivity).load(R.drawable.img_w_6).into(image2)
        }
        w4.apply {
            textWName.text = getString(R.string.count_down)
            textWSize.text = getString(R.string.size41)
            Glide.with(this@WidgetActivity).load(R.drawable.img_w_7).into(image1)
            Glide.with(this@WidgetActivity).load(R.drawable.img_w_8).into(image2)
        }
    }

    private fun setupEvents() = with(viewBinding) {
        header.button1.setOnClickListener { finish() }
        w1.layoutWidgetRoot.setOnClickListener {
            promptAddWidget(StandardWidget::class.java)
        }
        w2.layoutWidgetRoot.setOnClickListener {
            promptAddWidget(LiteWidget::class.java)
        }
        w3.layoutWidgetRoot.setOnClickListener {
            promptAddWidget(MonthWidget::class.java)
        }
        w4.layoutWidgetRoot.setOnClickListener {
            promptAddWidget(CountdownWidget::class.java)
        }
    }

    private fun promptAddWidget(component: Class<*>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mAppWidgetManager = getSystemService(AppWidgetManager::class.java)
            val myProvider = ComponentName(this, component)
            if (mAppWidgetManager.isRequestPinAppWidgetSupported) {
                val pinnedWidgetCallbackIntent =
                    Intent(this, component)
                val successCallback = PendingIntent.getBroadcast(
                    this, 0,
                    pinnedWidgetCallbackIntent, FLAG_IMMUTABLE
                )
                mAppWidgetManager.requestPinAppWidget(myProvider, Bundle(), successCallback)
            }
        }
    }
}
