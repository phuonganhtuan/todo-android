package com.trustedapp.todolist.planner.reminders.utils

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.data.models.entity.RingtoneEntity
import com.trustedapp.todolist.planner.reminders.data.models.entity.RingtoneEntityTypeEnum
import com.trustedapp.todolist.planner.reminders.data.models.entity.SYSTEM_RINGTONE_ID
import com.trustedapp.todolist.planner.reminders.data.models.entity.TODO_DEFAULT_RINGTONE_ID
import com.trustedapp.todolist.planner.reminders.data.models.model.SnoozeAfterModel
import com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.DefaultReminderTypeEnum
import com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.listSnoozeAfter
import com.trustedapp.todolist.planner.reminders.setting.DateFormat
import com.trustedapp.todolist.planner.reminders.setting.FirstDayOfWeek
import com.trustedapp.todolist.planner.reminders.setting.TimeFormat

object SPUtils {

    private const val TODO_SP_KEY = "TodoSP"
    private const val RECENT_SEARCH_KEY = "RecentSearchKey"
    private const val RECENT_JOIN = "-.,*"
    private const val FIRST_TIME_KEY = "IsFirstTime"
    const val KEY_INTER_SPLASH = "inter_splash"
    const val KEY_BANNER = "banner"
    const val KEY_NATIVE_LANGUAGE = "native_language"
    const val KEY_INTER_INSERT = "inter_insert"
    const val KEY_INTER_THEME = "inter_theme"
    private const val KEY_NUMBER_CREATE_TASK = "number_create_task"

    private const val THEME_COLOR_KEY = "theme_color"
    private const val THEME_TEXTURE_KEY = "theme_texture"
    private const val THEME_SCENERY_KEY = "theme_scenery"

    private const val CURRENT_LANG_KEY = "CurrentKey"

    private const val IS_ALLOW_NOTIFICATION = "is_allow_notification"
    private const val IS_IGNORE_BATTERY = "is_ignore_battery"
    private const val IS_FLOAT_WINDOW = "is_float_window"

    private const val DEFAULT_REMINDER_TYPE = "default_reminder_type"
    private const val DEFAULT_NOTIFICATION_RINGTONE = "default_notification_ringtone"
    private const val DEFAULT_ALARM_RINGTONE = "default_alarm_ringtone"

    private const val IS_SCREENLOCK_TASK_REMINDER = "is_screenlock_task_reminder"
    private const val IS_ADD_TASK_FROM_NOTIFICATION_BAR = "is_add_task_from_notification_bar"

    private const val IS_SNOOZE_TASK_REMINDER = "is_snooze_task_reminder"
    private const val SNOOZE_AFTER_VALUE = "snooze_after_value"

    private const val IS_TODO_REMINDER = "is_todo_reminder"
    private const val DAILY_REMINDER_RINGTONE = "daily_reminder_ringtone"

    const val FIRST_DAY_OF_WEEK_SETTING = "FirstDayOfWeek"
    const val TIME_FORMAT_SETTING = "TimeFormat"
    const val DATE_FORMAT_SETTING = "DateFormat"

    fun getSavedTheme(context: Context): Triple<Int, Int, Int> {
        val sp = context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
        val color = sp.getInt(THEME_COLOR_KEY, 0)
        val texture = sp.getInt(THEME_TEXTURE_KEY, -1)
        val scenery = sp.getInt(THEME_SCENERY_KEY, -1)
        return Triple(color, texture, scenery)
    }

    fun saveTheme(context: Context, color: Int, texture: Int, scenery: Int) =
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .edit()
            .putInt(THEME_COLOR_KEY, color)
            .putInt(THEME_TEXTURE_KEY, texture)
            .putInt(THEME_SCENERY_KEY, scenery)
            .apply()

    fun saveRecentSearch(context: Context, recents: List<String>) =
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .edit()
            .putString(RECENT_SEARCH_KEY, recents.joinToString(RECENT_JOIN))
            .apply()

    fun getRecentSearch(context: Context): List<String> {
        val result = context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .getString(RECENT_SEARCH_KEY, "")?.split(RECENT_JOIN) ?: emptyList()
        if (result.size == 1 && result[0].isEmpty()) return emptyList()
        return result
    }

    fun saveFirstTimeLaunched(context: Context) {
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(FIRST_TIME_KEY, false)
            .apply()
    }

    fun isFirstTime(context: Context) =
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .getBoolean(FIRST_TIME_KEY, true)

    fun getNumberNewTask(context: Context) =
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .getInt(KEY_NUMBER_CREATE_TASK, -1)

    fun setNumberNewTask(context: Context, numberOfTask: Int) =
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE).edit()
            .putInt(KEY_NUMBER_CREATE_TASK, numberOfTask).apply()

    fun getRemoteConfig(context: Context, name: String): Boolean {
        return context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .getBoolean(name, false)
    }

    fun setRemoteConfig(context: Context, name: String, value: Boolean) {
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE).edit()
            .putBoolean(name, value).apply()
    }

    fun saveCurrentLang(context: Context, langCode: String) =
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .edit()
            .putString(CURRENT_LANG_KEY, langCode)
            .apply()

    fun getCurrentLang(context: Context) =
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .getString(CURRENT_LANG_KEY, "")

    fun getIsAllowNotification(context: Context): Boolean {
        try {
            return NotificationManagerCompat.from(context).areNotificationsEnabled()
        } catch (e: Exception) {
            return context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
                .getBoolean(
                    IS_ALLOW_NOTIFICATION,
                    NotificationManagerCompat.from(context).areNotificationsEnabled()
                )
        }
    }

    fun setIsAllowNotification(context: Context, value: Boolean) {
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(IS_ALLOW_NOTIFICATION, value)
            .apply()
    }

    fun getIsIgnoreBattery(context: Context): Boolean {
        try {
            val pm = context?.applicationContext?.getSystemService(Context.POWER_SERVICE) as PowerManager
            return pm.isIgnoringBatteryOptimizations(context?.packageName)
        } catch (e: Exception) {
            return context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
                .getBoolean(
                    IS_IGNORE_BATTERY,
                    false
                )
        }
    }

    fun setIsIgnoreBattery(context: Context, value: Boolean) {
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(IS_IGNORE_BATTERY, value)
            .apply()
    }

    fun getIsFloatWindow(context: Context): Boolean {
        try {
            return Settings.canDrawOverlays(context)
        } catch (e: Exception) {
            return context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
                .getBoolean(IS_FLOAT_WINDOW, Settings.canDrawOverlays(context))
        }
    }

    fun setIsFloatWindow(context: Context, value: Boolean) {
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(IS_FLOAT_WINDOW, value)
            .apply()
    }

    fun getDefaultRemminderType(context: Context): DefaultReminderTypeEnum {
        val currentValue = context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .getString(DEFAULT_REMINDER_TYPE, DefaultReminderTypeEnum.NOTIFICATION.name)
        return when (currentValue) {
            DefaultReminderTypeEnum.ALARM.name -> DefaultReminderTypeEnum.ALARM
            else -> DefaultReminderTypeEnum.NOTIFICATION
        }
    }

    fun setDefaultReminderType(context: Context, value: DefaultReminderTypeEnum) {
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .edit()
            .putString(DEFAULT_REMINDER_TYPE, value.name)
            .apply()
    }

    fun getDefaultNotificationRingtone(context: Context): RingtoneEntity? {
        val currentValue = context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .getString(
                DEFAULT_NOTIFICATION_RINGTONE, Gson().toJson(
                    RingtoneEntity(
                        TODO_DEFAULT_RINGTONE_ID,
                        context.getString(R.string.todo_default),
                        Uri.parse(context.getString(R.string.default_todo_ringtone_path))
                            .toString(),
                        RingtoneEntityTypeEnum.SYSTEM_RINGTONE
                    )
                )
            )
        return Gson().fromJson(currentValue, RingtoneEntity::class.java)
    }

    fun setDefaultNotificationRingtone(context: Context, value: RingtoneEntity) {
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .edit()
            .putString(DEFAULT_NOTIFICATION_RINGTONE, Gson().toJson(value))
            .apply()
    }

    fun getDefaultAlarmRingtone(context: Context): RingtoneEntity? {
        // select rington alarm default
        var currentValue: String?
        try {
            val defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                context.applicationContext,
                RingtoneManager.TYPE_RINGTONE
            )
            currentValue = context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
                .getString(
                    DEFAULT_ALARM_RINGTONE, Gson().toJson(
                        RingtoneEntity(
                            SYSTEM_RINGTONE_ID,
                            context.getString(R.string.system_default),
                            defaultRingtoneUri.toString(),
                            RingtoneEntityTypeEnum.SYSTEM_RINGTONE
                        )
                    )
                )

        } catch (e: Exception) {
            Log.e("loadLocalRingtonesUris", "defaultRingtoneUri ", e)
            currentValue = context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
                .getString(
                    DEFAULT_NOTIFICATION_RINGTONE, Gson().toJson(
                        RingtoneEntity(
                            TODO_DEFAULT_RINGTONE_ID,
                            context.getString(R.string.todo_default),
                            Uri.parse(context.getString(R.string.default_todo_ringtone_path))
                                .toString(),
                            RingtoneEntityTypeEnum.SYSTEM_RINGTONE
                        )
                    )
                )
        }
        return Gson().fromJson(currentValue, RingtoneEntity::class.java)
    }

    fun setDefaultAlarmRingtone(context: Context, value: RingtoneEntity) {
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .edit()
            .putString(DEFAULT_ALARM_RINGTONE, Gson().toJson(value))
            .apply()
    }

    fun getIsScreenlockTaskReminder(context: Context): Boolean {
        return context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .getBoolean(IS_SCREENLOCK_TASK_REMINDER, false)
    }

    fun setIsScreenLockTaskReminder(context: Context, value: Boolean) {
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(IS_SCREENLOCK_TASK_REMINDER, value)
            .apply()
    }

    fun getIsAddTaskFromNotificationBar(context: Context): Boolean {
        return context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .getBoolean(IS_ADD_TASK_FROM_NOTIFICATION_BAR, false)
    }

    fun setIsAddTaskFromNotificationBar(context: Context, value: Boolean) {
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(IS_ADD_TASK_FROM_NOTIFICATION_BAR, value)
            .apply()
    }

    fun getIsSnoozeTask(context: Context): Boolean {
        return context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .getBoolean(IS_SNOOZE_TASK_REMINDER, false)
    }

    fun setIsSnoozeTask(context: Context, value: Boolean) {
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(IS_SNOOZE_TASK_REMINDER, value)
            .apply()
    }

    fun getSnoozeAfterValue(context: Context): SnoozeAfterModel {
        val currentValue = context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .getString(SNOOZE_AFTER_VALUE, Gson().toJson(listSnoozeAfter[0]))
        return Gson().fromJson(currentValue, SnoozeAfterModel::class.java)
    }

    fun setSnoozeAfterValue(context: Context, value: SnoozeAfterModel) {
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .edit()
            .putString(
                SNOOZE_AFTER_VALUE, Gson().toJson(value)
            )
            .apply()
    }

    fun getIsTodoReminder(context: Context): Boolean {
        return context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .getBoolean(IS_TODO_REMINDER, false)
    }

    fun setIsTodoReminder(context: Context, value: Boolean) {
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(IS_TODO_REMINDER, value)
            .apply()
    }

    fun setDailyRingtone(context: Context, value: RingtoneEntity) {
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .edit()
            .putString(DAILY_REMINDER_RINGTONE, Gson().toJson(value))
            .apply()
    }

    fun getDailyRingtone(context: Context): RingtoneEntity? {
        // select rington alarm default
        var currentValue: String?
        try {
            val defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                context.applicationContext,
                RingtoneManager.TYPE_RINGTONE
            )
            currentValue = context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
                .getString(
                    DAILY_REMINDER_RINGTONE, Gson().toJson(
                        RingtoneEntity(
                            SYSTEM_RINGTONE_ID,
                            context.getString(R.string.system_default),
                            defaultRingtoneUri.toString(),
                            RingtoneEntityTypeEnum.SYSTEM_RINGTONE
                        )
                    )
                )

        } catch (e: Exception) {
            Log.e("loadLocalRingtonesUris", "defaultRingtoneUri ", e)
            currentValue = context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
                .getString(
                    DAILY_REMINDER_RINGTONE, Gson().toJson(
                        RingtoneEntity(
                            TODO_DEFAULT_RINGTONE_ID,
                            context.getString(R.string.todo_default),
                            Uri.parse("file:///android_asset/raw/to_do_default.mp3").toString(),
                            RingtoneEntityTypeEnum.SYSTEM_RINGTONE
                        )
                    )
                )
        }
        return Gson().fromJson(currentValue, RingtoneEntity::class.java)
    }

    fun saveTimeFormat(context: Context, format: String) =
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .edit()
            .putString(TIME_FORMAT_SETTING, format)
            .apply()

    fun getTimeFormat(context: Context) =
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .getString(TIME_FORMAT_SETTING, TimeFormat.DEFAULT.name)

    fun saveDateFormat(context: Context, format: String) =
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .edit()
            .putString(DATE_FORMAT_SETTING, format)
            .apply()

    fun getDateFormat(context: Context) =
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .getString(DATE_FORMAT_SETTING, DateFormat.DDMMYYYY.name)

    fun saveFirstDayOfWeek(context: Context, value: String) =
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .edit()
            .putString(FIRST_DAY_OF_WEEK_SETTING, value)
            .apply()

    fun getFirstDayOfWeek(context: Context) =
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .getString(FIRST_DAY_OF_WEEK_SETTING, FirstDayOfWeek.AUTO.name)
}
