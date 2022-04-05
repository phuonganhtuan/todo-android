package com.trustedapp.todolist.planner.reminders.screens.newtask

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.alarm.ScheduleHelper
import com.trustedapp.todolist.planner.reminders.data.models.entity.*
import com.trustedapp.todolist.planner.reminders.data.repository.TaskRepository
import com.trustedapp.todolist.planner.reminders.initdata.bms
import com.trustedapp.todolist.planner.reminders.initdata.cat1
import com.trustedapp.todolist.planner.reminders.initdata.taskDetail
import com.trustedapp.todolist.planner.reminders.initdata.tasks
import com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.DefaultReminderTypeEnum
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import com.trustedapp.todolist.planner.reminders.utils.FileUtils
import com.trustedapp.todolist.planner.reminders.utils.SPUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.Calendar.*
import javax.inject.Inject
import kotlin.random.Random

enum class CustomReminderTimeUnitEnum {
    MINUTE_UNIT {
        override fun getStringid(): Int = R.string.minutes
        override fun getOffset() = 60000
        override fun getIndex(): Int = 0
    },
    HOUR_UNIT {
        override fun getStringid(): Int = R.string.hours
        override fun getOffset() = 3600000
        override fun getIndex(): Int = 1
    },
    DAY_UNIT {
        override fun getStringid(): Int = R.string.days
        override fun getOffset(): Int = 86400000
        override fun getIndex(): Int = 2
    },
    WEEK_UNIT {
        override fun getStringid(): Int = R.string.weeks
        override fun getOffset(): Int = 604800000
        override fun getIndex(): Int = 3
    };

    abstract fun getStringid(): Int
    abstract fun getOffset(): Int
    abstract fun getIndex(): Int
}

enum class ReminderTypeEnum {
    NOTIFICATION {
        override fun getStringid(): Int = R.string.notification
        override fun getItemMenuId(): Int = R.id.option_notification
    },
    ALARM {
        override fun getStringid(): Int = R.string.alarm
        override fun getItemMenuId(): Int = R.id.option_alarm
    };

    abstract fun getStringid(): Int
    abstract fun getItemMenuId(): Int
}

enum class ReminderTimeEnum {
    NONE {
        override fun getStringid(): Int = -1
        override fun getItemMenuId(): Int = -1
    },
    SAME_DUE_DATE {
        override fun getStringid(): Int =
            R.string.same_with_due_date

        override fun getItemMenuId(): Int = R.id.option_same_with_due_date
    },
    FIVE_MINUTES_BEFORE {
        override fun getStringid(): Int =
            R.string.five_minutes_before

        override fun getItemMenuId(): Int = R.id.option_5_minutes_before
    },
    TEN_MINUTES_BEFORE {
        override fun getStringid(): Int =
            R.string.ten_minutes_before

        override fun getItemMenuId(): Int = R.id.option_10_minutes_before
    },
    FIFTEEN_MINUTES_BEFORE {
        override fun getStringid(): Int =
            R.string.fifteen_minutes_before

        override fun getItemMenuId(): Int = R.id.option_10_minutes_before
    },
    THIRTY_MINUTES_BEFORE {
        override fun getStringid(): Int =
            R.string.thirty_minutes_before

        override fun getItemMenuId(): Int = R.id.option_30_minutes_before
    },
    ONE_DAY_BEFORE {
        override fun getStringid(): Int =
            R.string.one_day_before

        override fun getItemMenuId(): Int = R.id.option_1_day_before
    },
    TWO_DAYS_BEFORE {
        override fun getStringid(): Int =
            R.string.two_days_before

        override fun getItemMenuId(): Int = R.id.option_2_day_before
    },
    CUSTOM_DAY_BEFORE {
        override fun getStringid(): Int =
            R.string.set_reminder_time

        override fun getItemMenuId(): Int = R.id.option_set_reminder_time
    };

    abstract fun getStringid(): Int
    abstract fun getItemMenuId(): Int
}

enum class RepeatAtEnum {
    NONE {
        override fun getStringid(): Int = -1
        override fun getItemMenuId(): Int = -1
    },
    HOUR {
        override fun getStringid(): Int = R.string.hour
        override fun getItemMenuId(): Int = R.id.option_hour
    },
    DAILY {
        override fun getStringid(): Int = R.string.daily
        override fun getItemMenuId(): Int = R.id.option_daily
    },
    WEEKLY {
        override fun getStringid(): Int = R.string.weekly
        override fun getItemMenuId(): Int = R.id.option_weekly
    },
    MONTHLY {
        override fun getStringid(): Int = R.string.monthly
        override fun getItemMenuId(): Int = R.id.option_monthly
    },
    YEARLY {
        override fun getStringid(): Int = R.string.yearly
        override fun getItemMenuId(): Int = R.id.option_yearly
    };

    abstract fun getStringid(): Int
    abstract fun getItemMenuId(): Int
}

@HiltViewModel
class NewTaskViewModel @Inject constructor(private val repository: TaskRepository) : ViewModel() {

    val isNewTask: StateFlow<Boolean> get() = _isNewTask
    private val _isNewTask = MutableStateFlow(false)

    val canAddSubTask: StateFlow<Boolean> get() = _canAddSubTask
    private val _canAddSubTask = MutableStateFlow(false)

    val selectedDate: StateFlow<Date?> get() = _selectedDate
    private val _selectedDate = MutableStateFlow<Date?>(null)

    val selectedHour: StateFlow<Int> get() = _selectedHour
    private val _selectedHour = MutableStateFlow(-1)

    val selectedMinute: StateFlow<Int> get() = _selectedMinute
    private val _selectedMinute = MutableStateFlow(-1)

    val isCheckedReminder: StateFlow<Boolean> get() = _isCheckedReminder
    private val _isCheckedReminder = MutableStateFlow(false)

    val selectedReminderTime: StateFlow<ReminderTimeEnum> get() = _selectedReminderTime
    private val _selectedReminderTime = MutableStateFlow(ReminderTimeEnum.FIVE_MINUTES_BEFORE)

    val isSelectCustomReminderTime: StateFlow<Boolean> get() = _isSelectCustomReminderTime
    private val _isSelectCustomReminderTime = MutableStateFlow(false)

    val customReminderTime: StateFlow<Int> get() = _customReminderTime
    private val _customReminderTime = MutableStateFlow(0)

    val customReminderTimeUnit: StateFlow<CustomReminderTimeUnitEnum> get() = _customReminderTimeUnit
    private val _customReminderTimeUnit = MutableStateFlow(CustomReminderTimeUnitEnum.MINUTE_UNIT)

    val selectedReminderType: StateFlow<ReminderTypeEnum> get() = _selectedReminderType
    private val _selectedReminderType = MutableStateFlow(ReminderTypeEnum.NOTIFICATION)

    val selectedReminderScreenLock: StateFlow<Boolean> get() = _selectedReminderScreenLock
    private val _selectedReminderScreenLock = MutableStateFlow(false)

    val isCheckedRepeat: StateFlow<Boolean> get() = _isCheckedRepeat
    private val _isCheckedRepeat = MutableStateFlow(false)

    val selectedRepeatAt: StateFlow<RepeatAtEnum> get() = _selectedRepeatAt
    private val _selectedRepeatAt = MutableStateFlow(RepeatAtEnum.HOUR)

    val categories: StateFlow<List<CategoryEntity>> get() = _categories
    private val _categories = repository
        .getCategories()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(2000),
            emptyList()
        )

    val selectedCatIndex: StateFlow<Int> get() = _selectedCatIndex
    private val _selectedCatIndex = MutableStateFlow(-1)

    val attachments: StateFlow<List<AttachmentEntity>> get() = _attachments
    private val _attachments = MutableStateFlow(emptyList<AttachmentEntity>())

    val isAdded: StateFlow<Boolean> get() = _isAdded
    private val _isAdded = MutableStateFlow(false)

    val validated: StateFlow<Boolean> get() = _validated
    private val _validated = MutableStateFlow(false)

    val isPressedCreateTask: StateFlow<Boolean> get() = _isPressedCreateTask
    private val _isPressedCreateTask = MutableStateFlow(false)

    val isShortMode: StateFlow<Boolean> get() = _isShortMode
    private val _isShortMode = MutableStateFlow(true)

    fun switchShortMode() {
        _isShortMode.value = !_isShortMode.value
    }

    fun setIsPressCreateTask(value: Boolean) {
        _isPressedCreateTask.value = value
    }

    fun setIsNewTask(context: Context, isNewTask: Boolean) {
        _isNewTask.value = isNewTask
        _selectedReminderType.value =
            if (SPUtils.getDefaultRemminderType(context) == DefaultReminderTypeEnum.NOTIFICATION) {
                ReminderTypeEnum.NOTIFICATION
            } else {
                ReminderTypeEnum.ALARM
            }
        _selectedReminderScreenLock.value = SPUtils.getIsScreenlockTaskReminder(context)
    }

    fun addSubTask() {
        val currentSubTasks = _subtasks.value.toMutableList()
        currentSubTasks.add(
            SubTaskEntity(
                name = "",
                isDone = false,
                taskId = taskId,
                id = Random.nextInt()
            )
        )
        _subtasks.value = currentSubTasks
    }

    fun selectCat(index: Int) {
        _selectedCatIndex.value = index
    }

    fun updateSubTask(index: Int, title: String) = viewModelScope.launch {
        _subtasks.value[index].name = title
        _canAddSubTask.value = !(_subtasks.value.size == 1 && _subtasks.value[0].name.isEmpty())
    }

    fun removeSubTask(index: Int) {
//        _subtasks.value.removeAt(index)
    }

    fun isCatNameExisted(name: String) =
        _categories.value.map { it.name.lowercase() }.contains(name.lowercase())

    fun createCategory(name: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val entity = CategoryEntity(name = name)
            repository.addCategory(entity)
        }
    }

    /**
     * Select date
     */
    fun selectDate(date: Date) {
        _selectedDate.value = date
    }

    /**
     * Select Hour
     */
    fun selectHour(hour: Int) {
        _selectedHour.value = hour
    }

    /**
     * Select minute
     */
    fun selectMinute(minute: Int) {
        _selectedMinute.value = minute
    }

    /**
     * select hour and minute
     */
    fun selectHourAndMinute(hour: Int, minute: Int) {
        selectHour(hour)
        selectMinute(minute)
    }

    /**
     * on/off Reminder switch
     */
    fun onCheckChangeReminder(checked: Boolean = false) {
        _isCheckedReminder.value = checked
    }

    /**
     * select reminder at
     */
    fun selectReminderAt(reminderTime: ReminderTimeEnum = ReminderTimeEnum.NONE) {
        _selectedReminderTime.value = reminderTime
    }

    /**
     * select custom reminder time
     */
    fun setIsSelectCustomReminderTime(isSelect: Boolean = false) {
        _isSelectCustomReminderTime.value = isSelect
    }

    /**
     * set custom reminder time
     */
    fun setCustomReminderTime(time: Int, unit: CustomReminderTimeUnitEnum) {
        _customReminderTime.value = time
        _customReminderTimeUnit.value = unit
    }

    /**
     * select reminder type
     */
    fun selectReminderType(reminderType: ReminderTypeEnum = ReminderTypeEnum.NOTIFICATION) {
        _selectedReminderType.value = reminderType
    }

    /**
     * select reminder screenlock
     */
    fun selectReminderScreenlock(isAllowScreenLock: Boolean = false) {
        _selectedReminderScreenLock.value = isAllowScreenLock
    }

    /**
     * reset reminder default
     */
    fun resetReminderDefault() {
        _isCheckedReminder.value = false
        _selectedReminderTime.value = ReminderTimeEnum.FIVE_MINUTES_BEFORE
        _selectedReminderType.value = ReminderTypeEnum.NOTIFICATION
        _selectedReminderScreenLock.value = false
    }


    /**
     * on/off Reminder switch
     */
    fun onCheckChangeRepeat(checked: Boolean = false) {
        _isCheckedRepeat.value = checked
    }

    /**
     * select repeat at
     */
    fun selectRepeatAt(repeatAt: RepeatAtEnum = RepeatAtEnum.NONE) {
        _selectedRepeatAt.value = repeatAt
    }

    /**
     * reset Repeat
     */
    fun resetRepeatDefault() {
        _selectedRepeatAt.value = RepeatAtEnum.HOUR
    }

    fun validate(title: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _validated.value = title.isNotEmpty()
        }
    }

    fun createTask(context: Context, title: String, note: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            var calendar: Calendar? = getInstance()
            if (_selectedDate.value != null) {
                calendar = Calendar.getInstance().apply { time = _selectedDate.value!! }
            }
            calendar?.apply {
                set(HOUR_OF_DAY, if (_selectedHour.value == -1) 0 else _selectedHour.value)
                set(MINUTE, if (_selectedMinute.value == -1) 0 else _selectedMinute.value)
                set(SECOND, 0)
            }
            val taskEntity = TaskEntity(
                title = title,
                categoryId = if (_selectedCatIndex.value == -1) null else _categories.value[_selectedCatIndex.value].id,
                calendar = calendar?.timeInMillis,
                isDone = false,
                isMarked = false,
                markId = null,
                dueDate = if (_selectedHour.value == -1) "" else DateTimeUtils.getComparableDateString(calendar?.time, isDefault = true),
            )
            val taskId = repository.addTask(taskEntity)
            _subtasks.value.filter { st -> st.name.isNotEmpty() }.forEach {
                it.taskId = taskId.toInt()
                repository.addSubTasks(it)
            }
            val taskDetail = TaskDetailEntity(
                taskId = taskId.toInt(),
                note = note,
                isReminder = _isCheckedReminder.value,
                isRepeat = _isCheckedRepeat.value,
            )
            repository.addTaskDetail(taskDetail)
            _attachments.value.forEach {
                it.taskId = taskId.toInt()
                repository.addAttachment(it)
            }
            if (_isCheckedReminder.value) {
                val reminder = ReminderEntity(
                    reminderType = _selectedReminderType.value.name,
                    reminderTime = _selectedReminderTime.value.name,
                    customReminderTime = _customReminderTime.value,
                    customReminderTimeUnit = _customReminderTimeUnit.value.name,
                    screenLockReminder = _selectedReminderScreenLock.value,
                    enableRepeat = _isCheckedRepeat.value,
                    time = calendar?.timeInMillis ?: 0,
                    taskId = taskId.toInt(),
                    repeatTime = if (_isCheckedRepeat.value) _selectedRepeatAt.value.name else RepeatAtEnum.NONE.name,
                )
                repository.addReminder(reminder)
                taskEntity.id = taskId.toInt()
                ScheduleHelper.addAlarm(context, taskEntity, reminder)
            }
            _isAdded.value = true
        }
    }

    // For task detail and edit task
    private var taskId = 0

    val _hasTime = MutableStateFlow(false)

    val isSaving: StateFlow<Boolean> get() = _isSaving
    private val _isSaving = MutableStateFlow(false)

    val isRemoved: StateFlow<Boolean> get() = _isRemoved
    private val _isRemoved = MutableStateFlow(false)

    val task: StateFlow<Task> get() = _task
    private val _task = MutableStateFlow(
        Task(
            task = tasks[0],
            category = cat1,
            detail = taskDetail,
            bookmark = bms[0]
        )
    )

    var selectedBookmark: BookmarkEntity? = null

    val bookmarks: StateFlow<List<BookmarkEntity>>
        get() = _bookmarks

    private val _bookmarks = repository.getBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val subtasks: StateFlow<List<SubTaskEntity>> get() = _subtasks
    private val _subtasks = MutableStateFlow(emptyList<SubTaskEntity>())

    val isEditing: StateFlow<Boolean> get() = _isEditing
    private val _isEditing = MutableStateFlow(false)

    fun toEditMode() {
        _isEditing.value = true
        _isSaving.value = false
    }

    fun toViewMode() {
        _isEditing.value = false
        _isSaving.value = true
    }

    fun markAsDone() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val task = _task.value.task.copy()
            task.isDone = !task.isDone
            repository.updateTask(task)
            _task.value = repository.getTask(taskId)
        }
    }

    fun initData(taskId: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            this@NewTaskViewModel.taskId = taskId
            _task.value = repository.getTask(taskId)
        }
        withContext(Dispatchers.IO) {
            _attachments.value = _task.value.attachments
            _subtasks.value = _task.value.subTasks
            val calendar =
                if (_task.value.task.calendar == null) null else Calendar.getInstance()
                    .apply { timeInMillis = _task.value.task.calendar ?: 0L }
            _selectedDate.value = calendar?.time
            if (_task.value.task.dueDate?.isEmpty() == true) {
                _selectedHour.value = -1
                _selectedMinute.value = -1
            } else {
                _selectedHour.value = calendar?.get(HOUR_OF_DAY) ?: -1
                _selectedMinute.value = calendar?.get(MINUTE) ?: -1
            }
            _isCheckedReminder.value = _task.value.detail.isReminder
            _isCheckedRepeat.value = _task.value.detail.isRepeat
            _selectedRepeatAt.value = when (_task.value.reminder?.repeatTime) {
                RepeatAtEnum.DAILY.name -> RepeatAtEnum.DAILY
                RepeatAtEnum.HOUR.name -> RepeatAtEnum.HOUR
                RepeatAtEnum.MONTHLY.name -> RepeatAtEnum.MONTHLY
                RepeatAtEnum.WEEKLY.name -> RepeatAtEnum.WEEKLY
                RepeatAtEnum.YEARLY.name -> RepeatAtEnum.YEARLY
                else -> RepeatAtEnum.HOUR
            }
            _selectedReminderTime.value = when (_task.value.reminder?.reminderTime) {
                ReminderTimeEnum.CUSTOM_DAY_BEFORE.name -> ReminderTimeEnum.CUSTOM_DAY_BEFORE
                ReminderTimeEnum.SAME_DUE_DATE.name -> ReminderTimeEnum.SAME_DUE_DATE
                ReminderTimeEnum.FIFTEEN_MINUTES_BEFORE.name -> ReminderTimeEnum.FIFTEEN_MINUTES_BEFORE
                ReminderTimeEnum.FIVE_MINUTES_BEFORE.name -> ReminderTimeEnum.FIVE_MINUTES_BEFORE
                ReminderTimeEnum.THIRTY_MINUTES_BEFORE.name -> ReminderTimeEnum.THIRTY_MINUTES_BEFORE
                ReminderTimeEnum.ONE_DAY_BEFORE.name -> ReminderTimeEnum.ONE_DAY_BEFORE
                ReminderTimeEnum.TWO_DAYS_BEFORE.name -> ReminderTimeEnum.TWO_DAYS_BEFORE
                ReminderTimeEnum.TEN_MINUTES_BEFORE.name -> ReminderTimeEnum.TEN_MINUTES_BEFORE
                ReminderTimeEnum.CUSTOM_DAY_BEFORE.name -> ReminderTimeEnum.CUSTOM_DAY_BEFORE
                else -> ReminderTimeEnum.FIVE_MINUTES_BEFORE
            }

            _customReminderTime.value = _task.value.reminder?.customReminderTime ?: 0
            _customReminderTimeUnit.value = when (_task.value.reminder?.customReminderTimeUnit) {
                CustomReminderTimeUnitEnum.MINUTE_UNIT.name -> CustomReminderTimeUnitEnum.MINUTE_UNIT
                CustomReminderTimeUnitEnum.HOUR_UNIT.name -> CustomReminderTimeUnitEnum.HOUR_UNIT
                CustomReminderTimeUnitEnum.DAY_UNIT.name -> CustomReminderTimeUnitEnum.DAY_UNIT
                CustomReminderTimeUnitEnum.WEEK_UNIT.name -> CustomReminderTimeUnitEnum.WEEK_UNIT
                else -> CustomReminderTimeUnitEnum.MINUTE_UNIT
            }

            _selectedReminderType.value = when (_task.value.reminder?.reminderType) {
                ReminderTypeEnum.NOTIFICATION.name -> ReminderTypeEnum.NOTIFICATION
                ReminderTypeEnum.ALARM.name -> ReminderTypeEnum.ALARM
                else -> ReminderTypeEnum.NOTIFICATION
            }
            _selectedReminderScreenLock.value = _task.value.reminder?.screenLockReminder ?: false
        }
    }

    fun removeAttachment(index: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            try {
                val atts = _attachments.value.toMutableList()
                atts.removeAt(index)
                _attachments.value = atts
            } catch (exception: Exception) {
            }
        }
    }

    fun addAttachment(atts: List<AttachmentEntity>) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val currentAtts = _attachments.value.toMutableList()
            currentAtts.addAll(atts)
            _attachments.value = currentAtts
        }
    }

    fun setSubTasks(subTask: List<SubTaskEntity>) = viewModelScope.launch {
        _subtasks.value = subTask
    }

    fun updateSubTaskTitle(index: Int, title: String) = viewModelScope.launch {
        try {
            _subtasks.value[index].name = title
        } catch (exception: Exception) {

        }
    }

    fun updateSubTaskState(index: Int, state: Boolean) = viewModelScope.launch {
        _subtasks.value[index].isDone = state
    }

    fun duplicateTask() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val newTask = _task.value.copy()
            val newTaskEntity = TaskEntity(
                title = newTask.task.title,
                categoryId = newTask.task.categoryId,
                calendar = newTask.task.calendar,
                dueDate = newTask.task.dueDate,
                isDone = newTask.task.isDone,
                isMarked = newTask.task.isMarked,
                markId = newTask.task.markId,
            )
            val taskId = repository.addTask(newTaskEntity)
            val newDetail = TaskDetailEntity(
                taskId = taskId.toInt(),
                note = newTask.detail.note,
                isReminder = newTask.detail.isReminder,
                isRepeat = newTask.detail.isRepeat,
            )
            repository.addTaskDetail(newDetail)
            newTask.attachments.forEach { att ->
                val newAtt = AttachmentEntity(
                    name = att.name,
                    extension = att.extension,
                    path = att.path,
                    taskId = taskId.toInt(),
                    type = att.type,
                )
                repository.addAttachment(newAtt)
            }
            newTask.subTasks.forEach { sub ->
                val newSubTask = SubTaskEntity(
                    name = sub.name,
                    taskId = taskId.toInt(),
                    isDone = sub.isDone,
                )
                repository.addSubTasks(newSubTask)
            }
        }
    }

    fun deleteTask() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            task.value.apply {
                attachments.forEach {
                    repository.deleteAttachment(it.id)
                }
                subTasks.forEach {
                    repository.deleteSubtask(it.id)
                }
                repository.deleteTaskDetail(taskDetail.id)
                repository.deleteTask(task.id)
                _isRemoved.value = true
            }
        }
    }

    fun updateBookmark() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _task.value.task.markId = selectedBookmark?.id
            _task.value.task.isMarked = true
            _task.value.bookmark = selectedBookmark
            repository.updateTask(_task.value.task)
        }
    }

    fun updateTask(context: Context, title: String, note: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _isAdded.value = false
            var calendar: Calendar? = getInstance()
            if (_selectedDate.value != null) {
                calendar = Calendar.getInstance().apply { time = _selectedDate.value!! }
            }
            calendar?.apply {
                set(HOUR_OF_DAY, if (_selectedHour.value == -1) 0 else _selectedHour.value)
                set(MINUTE, if (_selectedMinute.value == -1) 0 else _selectedMinute.value)
                set(SECOND, 0)
            }
            if (_task.value.reminder != null) {
                ScheduleHelper.cancelAlarm(context, _task.value.task, _task.value.reminder!!)
            }
            _task.value.task.apply {
                this.title = title
                categoryId =
                    if (_selectedCatIndex.value == -1) _task.value.task.categoryId else _categories.value[_selectedCatIndex.value].id
                this.calendar = calendar?.timeInMillis
                dueDate = if (_selectedHour.value == -1) "" else DateTimeUtils.getComparableDateString(calendar?.time, isDefault = true)
            }
            repository.updateTask(_task.value.task)
            _task.value.subTasks.forEach {
                repository.deleteSubtask(it.id)
            }
            _subtasks.value.filter { st -> st.name.isNotEmpty() }.forEach {
                val entity = SubTaskEntity(
                    name = it.name,
                    isDone = it.isDone,
                    taskId = taskId
                )
                repository.addSubTasks(entity)
            }
            _task.value.detail.apply {
                this.note = note
                isReminder = _isCheckedReminder.value
                isRepeat = _isCheckedRepeat.value
            }

            repository.updateTaskDetail(_task.value.detail)
            _task.value.attachments.forEach {
                repository.deleteAttachment(it.id)
            }
            _attachments.value.forEach {
                val entity = AttachmentEntity(
                    path = it.path,
                    extension = it.extension,
                    name = it.name,
                    type = it.type,
                    taskId = taskId
                )
                repository.addAttachment(entity)
            }
            if (!_isCheckedReminder.value) {
                repository.deleteReminder(taskId)
            } else {
                if (_task.value.reminder != null) {
                    _task.value.reminder?.apply {
                        reminderType = _selectedReminderType.value.name
                        reminderTime = _selectedReminderTime.value.name
                        customReminderTime = _customReminderTime.value
                        customReminderTimeUnit = _customReminderTimeUnit.value.name
                        screenLockReminder = _selectedReminderScreenLock.value
                        enableRepeat = _isCheckedRepeat.value
                        time = calendar?.timeInMillis ?: 0
                        repeatTime =
                            if (_isCheckedRepeat.value) _selectedRepeatAt.value.name else RepeatAtEnum.NONE.name
                        repository.updateReminder(this)
                        ScheduleHelper.addAlarm(context, _task.value.task, _task.value.reminder!!)
                    }
                } else {
                    val reminder = ReminderEntity(
                        reminderType = _selectedReminderType.value.name,
                        reminderTime = _selectedReminderTime.value.name,
                        customReminderTime = _customReminderTime.value,
                        customReminderTimeUnit = _customReminderTimeUnit.value.name,
                        screenLockReminder = _selectedReminderScreenLock.value,
                        enableRepeat = _isCheckedRepeat.value,
                        time = calendar?.timeInMillis ?: 0,
                        taskId = taskId,
                        repeatTime = if (_isCheckedRepeat.value) _selectedRepeatAt.value.name else RepeatAtEnum.NONE.name,
                    )
                    repository.addReminder(reminder)
                    ScheduleHelper.addAlarm(context, _task.value.task, reminder)
                }
            }

            _isAdded.value = true
        }
    }

    fun selectAttachments(context: Context, data: List<AttachmentEntity>) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val attachmentIds = attachments.value.map { it.id }
            val dataTmp = data.filter { !attachmentIds.contains(it.id) }.map { att ->
                att.copy().apply { path = FileUtils.copyFileToInternalStorage(context, att.path) }
            }

            _attachments.value += dataTmp
        }
    }
}
