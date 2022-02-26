package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder

import android.app.Activity
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.FileUtils
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.LoadDataState
import com.trustedapp.todolist.planner.reminders.data.models.entity.RingtoneEntity
import com.trustedapp.todolist.planner.reminders.data.models.entity.RingtoneEntityTypeEnum
import com.trustedapp.todolist.planner.reminders.data.models.entity.SYSTEM_RINGTONE_ID
import com.trustedapp.todolist.planner.reminders.data.models.entity.TODO_DEFAULT_RINGTONE_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URI
import javax.inject.Inject


@HiltViewModel
class NotiReminderViewModel @Inject constructor() : ViewModel() {
    val listSystemRingtone: StateFlow<List<RingtoneEntity>> get() = _listSystemtRingtone
    private val _listSystemtRingtone = MutableStateFlow(emptyList<RingtoneEntity>())

    val selectNotificationRingtone: StateFlow<RingtoneEntity?> get() = _selectNotificationRingtone
    private val _selectNotificationRingtone = MutableStateFlow<RingtoneEntity?>(null)

    val isLoading: StateFlow<LoadDataState> get() = _isLoading
    private val _isLoading = MutableStateFlow<LoadDataState>(LoadDataState.NONE)

    val isRecording: StateFlow<Boolean> get() = _isRecording
    private val _isRecording = MutableStateFlow(false)

    val listRecord: StateFlow<List<RingtoneEntity>> get() = _listRecord
    private val _listRecord = MutableStateFlow(emptyList<RingtoneEntity>())

    init {
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
            val uri = Uri.parse("file:///android_asset/raw/to_do_default.mp3")
            alarms.add(
                RingtoneEntity(
                    TODO_DEFAULT_RINGTONE_ID,
                    context.getString(R.string.todo_default),
                    uri,
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
    fun selectRingtoneEntity(entity: RingtoneEntity) {
        _selectNotificationRingtone.value = entity
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
                val file = File(com.trustedapp.todolist.planner.reminders.utils.FileUtils.getRealPathFromURI(context, entity.ringtoneUri))
                if (file.exists()) {
                    file.delete()
                }
            } catch (exception: Exception) {
                return@withContext
            }
            val list = _listRecord.value.toMutableList()
            list.remove(entity)
            _listRecord.value = list
        }
    }

    /**
     * query audio
     */
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun loadAudioFromStorage(context: Context): MutableList<RingtoneEntity> {
        val audioList = mutableListOf<RingtoneEntity>()
        try {
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                )
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Video.Media.DURATION,
                MediaStore.MediaColumns.DATA,
                MediaStore.Audio.Media.ALBUM
            )

            val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
            val absolutePath =
                context.getExternalFilesDir(null)?.absolutePath + "/TodoRecord"
            val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " +
                    MediaStore.Audio.Media.DATA + " LIKE '$absolutePath'"

            val dir = File(absolutePath)
            if (dir.exists()) {
                var increasingId = 0
                val customRingtones = dir.listFiles().filter { it.extension == "mp3" }.map {
                    increasingId += 1
                    RingtoneEntity(
                        increasingId,
                        it.name,
                        FileProvider.getUriForFile(
                            context,
                            context.packageName + ".provider",
                            it
                        ),
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
}