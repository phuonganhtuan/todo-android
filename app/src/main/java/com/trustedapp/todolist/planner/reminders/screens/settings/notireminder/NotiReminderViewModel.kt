package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.LoadDataState
import com.trustedapp.todolist.planner.reminders.data.models.entity.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            val alarmsCursor: Cursor = ringtoneMgr.cursor
            val alarmsCount: Int = alarmsCursor.getCount()
            if (alarmsCount == 0 && !alarmsCursor.moveToFirst()) {
                alarmsCursor.close()
                return ArrayList()
            }
            while (!alarmsCursor.isAfterLast() && alarmsCursor.moveToNext()) {
                val currentPosition: Int = alarmsCursor.getPosition()
                val rington = ringtoneMgr.getRingtone(currentPosition)
                val uri = ringtoneMgr.getRingtoneUri(currentPosition)

                alarms.add(
                    RingtoneEntity(
                        currentPosition,
                        rington.getTitle(context),
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
            _listRecord.value += withContext(Dispatchers.IO) {
                listOf(entity)
            }
        }
    }

    /**
     * remove record
     */
    fun removeRecord(entity: RingtoneEntity) {
        viewModelScope.launch {
            _listRecord.value += withContext(Dispatchers.IO) {
                _listRecord.value.filter { it.id != entity.id && it.type == entity.type }
            }
        }
    }

    /**
     * query audio
     */
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

// Display videos in alphabetical order based on their display name.
            val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
            val absolutePath =
                Environment.getExternalStorageDirectory().absolutePath + "/TodoRecord/%"
            val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " +
                    MediaStore.Audio.Media.DATA + " LIKE '$absolutePath'"

            val query = context.applicationContext.contentResolver.query(
                collection,
                projection,
                null,
                null,
                sortOrder
            )
            query?.use { cursor ->
                // Cache column indices.
                cursor?.let {
                    val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val nameColumn =
                        it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                    val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                    val durationColumn =
                        it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                    val absolutePathOfAudioColumn =
                        it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                    val bucketNameColumn =
                        it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                    var i = 1
                    while (it.moveToNext()) {
                        // Get values of columns for a given video.
                        val id = it.getLong(idColumn)
                        val name = it.getString(nameColumn)
                        val extension: String = name.substring(name.lastIndexOf("."))
                        val size = it.getString(sizeColumn)
                        val duration = cursor.getInt(durationColumn)
                        val absolutePathOfAudio = it.getString(absolutePathOfAudioColumn)
                        val bucketName = it.getString(bucketNameColumn)
                        val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        audioList += RingtoneEntity(
                            i,
                            name,
                            contentUri,
                            RingtoneEntityTypeEnum.RECORD
                        )
                        i++
                    }

                }
            }
            query?.close()
        } catch (e: java.lang.Exception) {
            Log.e("loadAudioFromStorage - e", e.message.toString())
        }
        Log.e("loadAudioFromStorage", audioList.toString())
        return audioList
    }

    fun getAllRecord(context: Context) {
        viewModelScope.launch {
            _listRecord.value += withContext(Dispatchers.IO) {
                loadAudioFromStorage(context)
            }
        }
    }
}