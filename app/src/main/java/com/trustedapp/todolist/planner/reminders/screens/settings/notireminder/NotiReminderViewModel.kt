package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trustedapp.todolist.planner.reminders.R
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
import javax.inject.Inject


@HiltViewModel
class NotiReminderViewModel @Inject constructor(): ViewModel(){
    val listSystemRingtone: StateFlow<List<RingtoneEntity>> get() = _listSystemtRingtone
    private val _listSystemtRingtone = MutableStateFlow(emptyList<RingtoneEntity>())

    val selectNotificationRingtone : StateFlow<RingtoneEntity?>get() = _selectNotificationRingtone
    private val _selectNotificationRingtone = MutableStateFlow<RingtoneEntity?>(null)

    init {
    }

    /**
     * query Rington
     */
    private fun loadLocalRingtonesUris(activity: Activity, context: Context): MutableList<RingtoneEntity> {
        val alarms: MutableList<RingtoneEntity> = ArrayList()

        // Get current ringtone system
        try {
            val defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                activity.getApplicationContext(),
                RingtoneManager.TYPE_RINGTONE
            )
            val defaultRingtone = RingtoneManager.getRingtone(activity, defaultRingtoneUri)
            alarms.add(RingtoneEntity(SYSTEM_RINGTONE_ID, context.getString(R.string.system_default), defaultRingtoneUri, RingtoneEntityTypeEnum.SYSTEM_RINGTONE))
        }catch (e: Exception){
            Log.e("loadLocalRingtonesUris", "defaultRingtoneUri ", e)
        }

        // Get Todo default ringtone
        try {
            val uri = Uri.parse("file:///android_asset/raw/to_do_default.mp3")
            alarms.add(RingtoneEntity(TODO_DEFAULT_RINGTONE_ID, context.getString(R.string.todo_default), uri, RingtoneEntityTypeEnum.SYSTEM_RINGTONE))
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

                alarms.add(RingtoneEntity(currentPosition, rington.getTitle(context), uri, RingtoneEntityTypeEnum.SYSTEM_RINGTONE))
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
        viewModelScope.launch {
            _listSystemtRingtone.value += withContext(Dispatchers.IO) {
                loadLocalRingtonesUris(activity, context)
            }
        }
    }

    /**
     * Set select entity
     */
    fun selectRingtoneEntity(entity: RingtoneEntity){
        _selectNotificationRingtone.value = entity
    }
}