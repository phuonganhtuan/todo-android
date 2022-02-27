package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder

import android.app.Activity
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.LoadDataState
import com.trustedapp.todolist.planner.reminders.data.models.entity.RingtoneEntity
import com.trustedapp.todolist.planner.reminders.data.models.entity.RingtoneEntityTypeEnum
import com.trustedapp.todolist.planner.reminders.data.models.entity.SYSTEM_RINGTONE_ID
import com.trustedapp.todolist.planner.reminders.data.models.entity.TODO_DEFAULT_RINGTONE_ID
import com.trustedapp.todolist.planner.reminders.data.models.model.SnoozeAfterModel
import com.trustedapp.todolist.planner.reminders.utils.Constants
import com.trustedapp.todolist.planner.reminders.utils.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

enum class DefaultReminderTypeEnum {
    NOTIFICATION,
    ALARM
}

@HiltViewModel
class NotiReminderViewModel @Inject constructor() : ViewModel() {
    // Notification Help
    val isAllowNotification: StateFlow<Boolean> get() = _isAllowNotification
    private val _isAllowNotification = MutableStateFlow(true)

    val isIgnoreBattery: StateFlow<Boolean> get() = _isIgnoreBattery
    private val _isIgnoreBattery = MutableStateFlow(false)

    val isFloatingWindow: StateFlow<Boolean> get() = _isFloatingWindow
    private val _isFloatingWindow = MutableStateFlow(false)

    val defaultReminderType: StateFlow<DefaultReminderTypeEnum> get() = _defaultReminderType
    private val _defaultReminderType = MutableStateFlow(DefaultReminderTypeEnum.NOTIFICATION)

    val isScreenLockTaskReminder: StateFlow<Boolean> get() = _isScreenLockTaskReminder
    private val _isScreenLockTaskReminder = MutableStateFlow(false)

    val isAddTaskFromNotificationBar: StateFlow<Boolean> get() = _isAddTaskFromNotificationBar
    private val _isAddTaskFromNotificationBar = MutableStateFlow(false)

    val listSystemRingtone: StateFlow<List<RingtoneEntity>> get() = _listSystemtRingtone
    private val _listSystemtRingtone = MutableStateFlow(emptyList<RingtoneEntity>())

    val selectNotificationRingtone: StateFlow<RingtoneEntity?> get() = _selectNotificationRingtone
    private val _selectNotificationRingtone = MutableStateFlow<RingtoneEntity?>(null)

    val selectAlarmRingtone: StateFlow<RingtoneEntity?> get() = _selectAlarmRingtone
    private val _selectAlarmRingtone = MutableStateFlow<RingtoneEntity?>(null)

    val isLoading: StateFlow<LoadDataState> get() = _isLoading
    private val _isLoading = MutableStateFlow<LoadDataState>(LoadDataState.NONE)

    val isRecording: StateFlow<Boolean> get() = _isRecording
    private val _isRecording = MutableStateFlow(false)

    val listRecord: StateFlow<List<RingtoneEntity>> get() = _listRecord
    private val _listRecord = MutableStateFlow(emptyList<RingtoneEntity>())

    val isSnoozeTaskReminder: StateFlow<Boolean> get() = _isSnoozeTaskReminder
    private val _isSnoozeTaskReminder = MutableStateFlow(false)

    val listSnoozeAfter: List<SnoozeAfterModel> = listOf(
        SnoozeAfterModel(1, R.string.five_minutes, 30000),
        SnoozeAfterModel(2, R.string.fifteen_minutes, 90000),
        SnoozeAfterModel(3, R.string.thirty_minutes, 180000),
        SnoozeAfterModel(4, R.string.one_hour, 360000),
    )
    val snoozeAfter: StateFlow<SnoozeAfterModel?> get() = _snoozeAfter
    private val _snoozeAfter = MutableStateFlow<SnoozeAfterModel?>(null)

    val isTodoReminder: StateFlow<Boolean> get() = _isTodoReminder
    private val _isTodoReminder = MutableStateFlow(false)

    val selectDailyRingtone: StateFlow<RingtoneEntity?> get() = _selectDailyRingtone
    private val _selectDailyRingtone = MutableStateFlow<RingtoneEntity?>(null)

    init {

    }

    fun setupData(context: Context, activity: Activity) {
        // Init notification help
        initNotificationHelp(context, activity)

        // Init Task Reminder
        initSelectDefaultNotificationRington(context, activity)
        initSelectDefaultAlarmRington(context, activity)
        initSnoozeAfter(context, activity)

        // Init Daily reminder
        initSelectDailyRington(context, activity)
    }

    private fun initNotificationHelp(context: Context, activity: Activity) {
        _isFloatingWindow.value = Settings.canDrawOverlays(context)
    }

    private fun initSelectDefaultNotificationRington(context: Context, activity: Activity) {
        // select ringron noti default
        _selectNotificationRingtone.value = RingtoneEntity(
            TODO_DEFAULT_RINGTONE_ID,
            context.getString(R.string.todo_default),
            Uri.parse("file:///android_asset/raw/to_do_default.mp3"),
            RingtoneEntityTypeEnum.SYSTEM_RINGTONE
        )
    }

    private fun initSnoozeAfter(context: Context, activity: Activity) {
        _snoozeAfter.value = listSnoozeAfter[0]
    }

    private fun initSelectDefaultAlarmRington(context: Context, activity: Activity) {
        // select rington alarm default
        try {
            val defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                activity.getApplicationContext(),
                RingtoneManager.TYPE_RINGTONE
            )
            val defaultRingtone = RingtoneManager.getRingtone(activity, defaultRingtoneUri)

            _selectAlarmRingtone.value = RingtoneEntity(
                SYSTEM_RINGTONE_ID,
                context.getString(R.string.system_default),
                defaultRingtoneUri,
                RingtoneEntityTypeEnum.SYSTEM_RINGTONE
            )
        } catch (e: Exception) {
            Log.e("loadLocalRingtonesUris", "defaultRingtoneUri ", e)
        }
    }

    private fun initSelectDailyRington(context: Context, activity: Activity) {
        // select rington alarm default
        try {
            val defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                activity.getApplicationContext(),
                RingtoneManager.TYPE_RINGTONE
            )
            val defaultRingtone = RingtoneManager.getRingtone(activity, defaultRingtoneUri)

            _selectDailyRingtone.value = RingtoneEntity(
                SYSTEM_RINGTONE_ID,
                context.getString(R.string.system_default),
                defaultRingtoneUri,
                RingtoneEntityTypeEnum.SYSTEM_RINGTONE
            )
        } catch (e: Exception) {
            Log.e("loadLocalRingtonesUris", "defaultRingtoneUri ", e)
        }
    }

    /**
     * set default type
     */
    fun setDefaultType(value: DefaultReminderTypeEnum) {
        _defaultReminderType.value = value
    }

    /**
     * query Rington
     */
    private fun loadLocalRingtonesUris(
        activity: Activity,
        context: Context
    ): MutableList<RingtoneEntity> {
        val alarms: MutableList<RingtoneEntity> = ArrayList()

        // Get current ringtone system
        try {
            val defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                activity.getApplicationContext(),
                RingtoneManager.TYPE_RINGTONE
            )
            val defaultRingtone = RingtoneManager.getRingtone(activity, defaultRingtoneUri)
            alarms.add(
                RingtoneEntity(
                    SYSTEM_RINGTONE_ID,
                    context.getString(R.string.system_default),
                    defaultRingtoneUri,
                    RingtoneEntityTypeEnum.SYSTEM_RINGTONE
                )
            )
        } catch (e: Exception) {
            Log.e("loadLocalRingtonesUris", "defaultRingtoneUri ", e)
        }

        // Get Todo default ringtone
        try {
            alarms.add(
                RingtoneEntity(
                    TODO_DEFAULT_RINGTONE_ID,
                    context.getString(R.string.todo_default),
                    Uri.parse("file:///android_asset/raw/to_do_default.mp3"),
                    RingtoneEntityTypeEnum.SYSTEM_RINGTONE
                )
            )
        } catch (e: Exception) {
            Log.e("loadLocalRingtonesUris", "default ringtone ", e)
        }

        // Get all system ringtone
        try {
            val ringtoneMgr = RingtoneManager(activity)
            ringtoneMgr.setType(RingtoneManager.TYPE_RINGTONE)
            val alarmsCursor = ringtoneMgr.cursor
            val alarmsCount = alarmsCursor.count
            if (alarmsCount == 0 && !alarmsCursor.moveToFirst()) {
                alarmsCursor.close()
                return ArrayList()
            }
            while (!alarmsCursor.isAfterLast && alarmsCursor.moveToNext()) {
                val currentPosition = alarmsCursor.position
                val ringtone = ringtoneMgr.getRingtone(currentPosition)
                val uri = ringtoneMgr.getRingtoneUri(currentPosition)

                alarms.add(
                    RingtoneEntity(
                        currentPosition,
                        ringtone.getTitle(context),
                        uri,
                        RingtoneEntityTypeEnum.SYSTEM_RINGTONE
                    )
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        Log.e("loadLocalRingtonesUris - alarms", alarms.toString())
        return alarms
    }


    /**
     * get all rington
     */
    fun getAllRington(activity: Activity, context: Context) {
        showLoading()
        viewModelScope.launch {
            _listSystemtRingtone.value += withContext(Dispatchers.IO) {
                loadLocalRingtonesUris(activity, context)
            }
            hiddenLoading()
        }
    }

    /**
     * Set select entity
     */
    fun selectDefaultRingtoneEntity(entity: RingtoneEntity, type: DefaultReminderTypeEnum) {
        if (type == DefaultReminderTypeEnum.ALARM) {
            _selectAlarmRingtone.value = entity
        } else {
            _selectNotificationRingtone.value = entity
        }
    }

    /**
     * Show loading
     */
    fun showLoading() {
        _isLoading.value = LoadDataState.LOADING
    }

    /**
     * hidden Loading
     */
    fun hiddenLoading() {
        _isLoading.value = LoadDataState.SUCCESS
    }

    /**
     * set is Recording
     */
    fun setRecording(value: Boolean = false) {
        _isRecording.value = value
    }

    /**
     * add record
     */
    fun addRecord(entity: RingtoneEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val list = _listRecord.value.toMutableList()
                list.add(0, entity)
                _listRecord.value = list
            }
        }
    }

    /**
     * remove record
     */
    fun removeRecord(context: Context, entity: RingtoneEntity) = viewModelScope.launch {
        withContext(Dispatchers.IO) {

            try {
                val file = File(
                    FileUtils.getRealPathFromURI(
                        context,
                        entity.ringtoneUri
                    ).toString().replace("file:/", "").replace("content:/", "")
                )
                if (file.exists()) {
                    file.delete()

                    val list = _listRecord.value.toMutableList()
                    list.remove(entity)
                    _listRecord.value = list
                }
            } catch (exception: Exception) {
                return@withContext
            }
        }
    }

    /**
     * query audio
     */
    private fun loadAudioFromStorage(context: Context): MutableList<RingtoneEntity> {
        val audioList = mutableListOf<RingtoneEntity>()
        try {
            val absolutePath =
                context.getExternalFilesDir(null)?.absolutePath + Constants.RECORD_RINGTONE_FOLDER
            val dir = File(absolutePath)
            if (dir.exists()) {
                var increasingId = 0
                val customRingtones = dir.listFiles()
                    .filter {
                        it.extension == Constants.RECORD_RINGTONE_EXTENSION && it.name.contains(
                            Constants.RECORD_RINGTONE_PREFIX
                        )
                    }.map {
                        increasingId += 1
                        RingtoneEntity(
                            increasingId,
                            it.name,
                            Uri.fromFile(it),
                            RingtoneEntityTypeEnum.RECORD
                        )
                    }
                return customRingtones.toMutableList()
            }
        } catch (e: java.lang.Exception) {
            Log.e("loadAudioFromStorage - e", e.message.toString())
        }
        Log.e("loadAudioFromStorage", audioList.toString())
        return audioList
    }

    fun getAllRecord(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _listRecord.value = loadAudioFromStorage(context).asReversed()
            }
        }
    }

    /**
     * set is screenlock
     */
    fun setIsScreenlock(value: Boolean) {
        _isScreenLockTaskReminder.value = value
    }

    /**
     * set is screenlock
     */
    fun setIsAddTaskFromNotiBar(value: Boolean) {
        _isAddTaskFromNotificationBar.value = value
    }

    /**
     * set snooze task switch
     */
    fun setIsSnoozeTaskReminder(value: Boolean) {
        _isSnoozeTaskReminder.value = value
    }

    /**
     * set Snooze after
     */
    fun setSnoozeAfter(value: SnoozeAfterModel) {
        _snoozeAfter.value = value
    }

    /**
     * set isTodo Reminder
     */
    fun setIsTodoReminder(value: Boolean) {
        _isTodoReminder.value = value
    }

    /**
     * set isTodo Reminder
     */
    fun setIsAllowNotification(value: Boolean) {
        _isAllowNotification.value = value
    }

    /**
     * set isTodo Reminder
     */
    fun setIsIgnoreBattery(value: Boolean) {
        _isIgnoreBattery.value = value
    }

    /**
     * set isTodo Reminder
     */
    fun setIsFloatWindow(value: Boolean) {
        _isFloatingWindow.value = value
    }

    /**
     * Set dailyringtone entity
     */
    fun selectDailyRingtoneEntity(entity: RingtoneEntity) {
        _selectDailyRingtone.value = entity
    }
}