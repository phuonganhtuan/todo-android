package com.example.todo.alarm

import android.content.Context
import android.os.*
import android.view.WindowManager
import com.example.todo.base.BaseActivity
import com.example.todo.databinding.ActivityAlarmBinding
import com.example.todo.utils.Constants
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AlarmActivity : BaseActivity<ActivityAlarmBinding>() {

    private  val handler = Handler(Looper.getMainLooper())

    override fun inflateViewBinding() = ActivityAlarmBinding.inflate(layoutInflater)
    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {
        initViews()
        setupEvents()
    }

    private fun initViews() = with(viewBinding) {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
        val bundle = intent.extras
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setTurnScreenOn(true)
            setShowWhenLocked(bundle?.getBoolean(Constants.KEY_SCREEN_LOCK_ENABLED, false) ?: false)
        }
        val taskTitle = bundle?.getString(Constants.KEY_TASK_TITLE)
        val taskTime = bundle?.getString(Constants.KEY_TASK_TIME)
        textTaskTime.text = taskTime?.replace(" ", System.lineSeparator())
        textTaskName.text = taskTitle
        content.startRippleAnimation()
        vibratePhone()
    }

    private fun setupEvents() = with(viewBinding) {
        buttonDismiss.setOnClickListener {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.cancel()
            handler.removeCallbacksAndMessages(null)
            finish()
        }
    }

    private fun vibratePhone() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val pattern = LongArray(2).apply {
            set(0, 200)
            set(1, 400)
        }
        val runnable = object : Runnable {

            override fun run() {
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
                } else {
                    vibrator.vibrate(pattern, 0)
                }
                handler.postDelayed(this, 2000)
            }
        }
        handler.post(runnable)
    }
}
