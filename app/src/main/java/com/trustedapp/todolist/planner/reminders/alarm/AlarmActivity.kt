package com.trustedapp.todolist.planner.reminders.alarm

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.view.WindowManager
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.data.models.entity.TODO_DEFAULT_RINGTONE_ID
import com.trustedapp.todolist.planner.reminders.databinding.ActivityAlarmBinding
import com.trustedapp.todolist.planner.reminders.utils.Constants
import com.trustedapp.todolist.planner.reminders.utils.SPUtils
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.show
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AlarmActivity : BaseActivity<ActivityAlarmBinding>() {

    private val handler = Handler(Looper.getMainLooper())

    private var mediaPlayer = MediaPlayer()

    override fun inflateViewBinding() = ActivityAlarmBinding.inflate(layoutInflater)
    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {
        initViews()
        setupEvents()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    private fun initViews() = with(viewBinding) {
        if (SPUtils.getIsSnoozeTask(this@AlarmActivity)) {
            buttonSnooze.show()
        } else {
            buttonSnooze.gone()
        }
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
        val bundle = intent.extras
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setTurnScreenOn(true)
            if (SPUtils.getIsScreenlockTaskReminder(this@AlarmActivity)) {
                setShowWhenLocked(
                    bundle?.getBoolean(Constants.KEY_SCREEN_LOCK_ENABLED, false) ?: false
                )
            } else {
                setShowWhenLocked(false)
            }
        }
        val taskTitle = bundle?.getString(Constants.KEY_TASK_TITLE)
        val taskTime = bundle?.getString(Constants.KEY_TASK_TIME)
        textTaskTime.text = taskTime?.replace(" ", System.lineSeparator())
        textTaskName.text = taskTitle
        playRingtone()
        content.startRippleAnimation()
        vibratePhone()
    }

    private fun playRingtone() {
        val ringtone = SPUtils.getDefaultAlarmRingtone(this@AlarmActivity)
        try {
            mediaPlayer = if (ringtone?.id == TODO_DEFAULT_RINGTONE_ID) {
                MediaPlayer.create(applicationContext, R.raw.to_do_default)

            } else {
                MediaPlayer.create(applicationContext, Uri.parse(ringtone?.ringtoneUri))
            }

            mediaPlayer.setOnPreparedListener {
                Handler(Looper.getMainLooper()).postDelayed({
                    it.stop()
                }, it.duration.toLong())
                it.start()
            }
        } catch (exception: Exception) {
        }
    }

    private fun setupEvents() = with(viewBinding) {
        buttonDismiss.setOnClickListener {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.cancel()
            handler.removeCallbacksAndMessages(null)
            finish()
        }
        buttonSnooze.setOnClickListener {

            val snoozeAfter = SPUtils.getSnoozeAfterValue(this@AlarmActivity).offset
            val taskId = intent.extras?.getInt(Constants.KEY_TASK_ID) ?: 0
            val taskTitle = intent.extras?.getString(Constants.KEY_TASK_TITLE) ?: ""
            val taskCalendar =
                intent.extras?.getLong(Constants.KEY_TASK_TIME) ?: System.currentTimeMillis()
            val reminderType = intent.extras?.getString(Constants.KEY_REMINDER_TYPE) ?: ""
            val screenLockReminder =
                intent.extras?.getBoolean(Constants.KEY_SCREEN_LOCK_ENABLED) ?: false
            ScheduleHelper.createSnoozeAlarm(
                this@AlarmActivity,
                snoozeAfter,
                taskId,
                taskTitle,
                taskCalendar,
                reminderType,
                screenLockReminder
            )
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
