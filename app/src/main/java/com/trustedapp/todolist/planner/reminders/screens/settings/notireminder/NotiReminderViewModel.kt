package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder

import android.app.Activity
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
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
import com.trustedapp.todolist.planner.reminders.utils.SPUtils
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

val listSnoozeAfter: List<SnoozeAfterModel> = listOf(
    SnoozeAfterModel(1, R.string.five_minutes, 30000),
    SnoozeAfterModel(2, R.string.fifteen_minutes, 90000),
    SnoozeAfterModel(3, R.string.thirty_minutes, 180000),
    SnoozeAfterModel(4, R.string.one_hour, 360000),
)

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
        _isAllowNotification.value = SPUtils.getIsAllowNotification(context)
        _isIgnoreBattery.value = SPUtils.getIsIgnoreBattery(context)
        _isFloatingWindow.value = SPUtils.getIsFloatWindow(context)

        // Init Task Reminder
        _defaultReminderType.value = SPUtils.getDefaultRemminderType(context)
        _selectNotificationRingtone.value = SPUtils.getDefaultNotificationRingtone(context)
        _selectAlarmRingtone.value = SPUtils.getDefaultAlarmRingtone(context)
        _isScreenLockTaskReminder.value = SPUtils.getIsScreenlockTaskReminder(context)
        _isAddTaskFromNotificationBar.value = SPUtils.getIsAddTaskFromNotificationBar(context)
        _isSnoozeTaskReminder.value = SPUtils.getIsSnoozeTask(context)
        _snoozeAfter.value = SPUtils.getSnoozeAfterValue(context)

        // Daily Reminder
        _isTodoReminder.value = SPUtils.getIsTodoReminder(context)
        _selectDailyRingtone.value = SPUtils.getDailyRingtone(context)
    }

    /**
     * set default type
     */
    fun setDefaultType(context: Context, value: DefaultReminderTypeEnum) {
        _defaultReminderType.value = value
        SPUtils.setDefaultReminderType(context, value)
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
                context,
                RingtoneManager.TYPE_RINGTONE
            )
            alarms.add(
                RingtoneEntity(
                    SYSTEM_RINGTONE_ID,
                    context.getString(R.string.system_default),
                    defaultRingtoneUri.toString(),
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
                    Uri.parse(context.getString(R.string.default_todo_ringtone_path)).toString(),
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
                return alarms
            }
            while (!alarmsCursor.isAfterLast && alarmsCursor.moveToNext()) {
                val currentPosition = alarmsCursor.position
                val ringtone = ringtoneMgr.getRingtone(currentPosition)
                val uri = ringtoneMgr.getRingtoneUri(currentPosition)

                alarms.add(
                    RingtoneEntity(
                        currentPosition,
                        ringtone.getTitle(context),
                        uri.toString(),
                        RingtoneEntityTypeEnum.SYSTEM_RINGTONE
                    )
                )
            }
            alarmsCursor.close()
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
    fun selectDefaultRingtoneEntity(
        context: Context,
        entity: RingtoneEntity,
        type: DefaultReminderTypeEnum
    ) {
        if (type == DefaultReminderTypeEnum.ALARM) {
            _selectAlarmRingtone.value = entity
            SPUtils.setDefaultAlarmRingtone(context, entity)
        } else {
            _selectNotificationRingtone.value = entity
            SPUtils.setDefaultNotificationRingtone(context, entity)
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
                        Uri.parse(entity.ringtoneUri)
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
                            Uri.fromFile(it).toString(),
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
    fun setIsScreenlock(context: Context, value: Boolean) {
        _isScreenLockTaskReminder.value = value
        SPUtils.setIsScreenLockTaskReminder(context, value)
    }

    /**
     * set is screenlock
     */
    fun setIsAddTaskFromNotiBar(context: Context, value: Boolean) {
        _isAddTaskFromNotificationBar.value = value
        SPUtils.setIsAddTaskFromNotificationBar(context, value)
    }

    /**
     * set snooze task switch
     */
    fun setIsSnoozeTaskReminder(context: Context, value: Boolean) {
        _isSnoozeTaskReminder.value = value
        SPUtils.setIsSnoozeTask(context, value)
    }

    /**
     * set Snooze after
     */
    fun setSnoozeAfter(context: Context, value: SnoozeAfterModel) {
        _snoozeAfter.value = value
        SPUtils.setSnoozeAfterValue(context, value)
    }

    /**
     * set isTodo Reminder
     */
    fun setIsTodoReminder(context: Context, value: Boolean) {
        _isTodoReminder.value = value
        SPUtils.setIsTodoReminder(context, value)
    }

    /**
     * set isTodo Reminder
     */
    fun setIsAllowNotification(context: Context, value: Boolean) {
        _isAllowNotification.value = value
        SPUtils.setIsAllowNotification(context, value)
    }

    /**
     * set isTodo Reminder
     */
    fun setIsIgnoreBattery(context: Context, value: Boolean) {
        _isIgnoreBattery.value = value
        SPUtils.setIsIgnoreBattery(context, value)
    }

    /**
     * set isTodo Reminder
     */
    fun setIsFloatWindow(context: Context, value: Boolean) {
        _isFloatingWindow.value = value
        SPUtils.setIsFloatWindow(context, value)
    }

    /**
     * Set dailyringtone entity
     */
    fun selectDailyRingtoneEntity(context: Context, entity: RingtoneEntity) {
        _selectDailyRingtone.value = entity
        SPUtils.setDailyRingtone(context, entity)
    }
}