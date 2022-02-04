package com.example.todo.screens.newtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.R
import com.example.todo.data.models.entity.CategoryEntity
import com.example.todo.data.models.entity.SubTaskEntity
import com.example.todo.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.random.Random


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
        override fun getItemMenuId(): Int = -1
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
    val subTasks: StateFlow<List<SubTaskEntity>> get() = _subTasks
    private val _subTasks = MutableStateFlow<MutableList<SubTaskEntity>>(mutableListOf())

    val canAddSubTask: StateFlow<Boolean> get() = _canAddSubTask
    private val _canAddSubTask = MutableStateFlow(false)

    val selectedDate: StateFlow<Date> get() = _selectedDate
    private val _selectedDate = MutableStateFlow(Calendar.getInstance().time)

    val selectedHour: StateFlow<Int> get() = _selectedHour
    private val _selectedHour = MutableStateFlow(-1)

    val selectedMinute: StateFlow<Int> get() = _selectedMinute
    private val _selectedMinute = MutableStateFlow(-1)

    val isCheckedReminder: StateFlow<Boolean> get() = _isCheckedReminder
    private val _isCheckedReminder = MutableStateFlow(false)

    val selectedReminderTime: StateFlow<ReminderTimeEnum> get() = _selectedReminderTime
    private val _selectedReminderTime = MutableStateFlow(ReminderTimeEnum.FIVE_MINUTES_BEFORE)

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

    init {
        addSubTask()
    }

    fun addSubTask() {
        val currentSubTasks = _subTasks.value.toMutableList()
        currentSubTasks.add(
            SubTaskEntity(
                name = "",
                isDone = false,
                taskId = 0,
                id = Random.nextInt()
            )
        )
        _subTasks.value = currentSubTasks
    }

    fun selectCat(index: Int) {
        _selectedCatIndex.value = index
    }

    fun updateSubTask(index: Int, title: String) = viewModelScope.launch {
        _subTasks.value[index].name = title
        _canAddSubTask.value = !(_subTasks.value.size == 1 && _subTasks.value[0].name.isEmpty())
    }

    fun removeSubTask(index: Int) {
        _subTasks.value.removeAt(index)
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
    fun resetReminderDefault(){
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
    fun resetRepeatDefault(){
        _selectedRepeatAt.value = RepeatAtEnum.HOUR
    }

    fun createTask() {

    }
}
