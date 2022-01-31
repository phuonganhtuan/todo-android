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

enum class ReminderStatusEnum{
    OFF,
    ON
}

enum class ReminderTypeEnum{
    NOTIFICATION,
    ALARM
}

enum class ReminderTimeEnum{
    NONE {
        override fun getStringid(): Int  = -1
    },
    SAME_DUE_DATE {
        override fun getStringid(): Int =
            R.string.same_with_due_date
    },
    FIVE_MINUTES_BEFORE{
        override fun getStringid(): Int =
            R.string.five_minutes_before
    },
    TEN_MINUTES_BEFORE{
        override fun getStringid(): Int =
            R.string.ten_minutes_before
    },
    FIFTEEN_MINUTES_BEFORE{
        override fun getStringid(): Int =
            R.string.fifteen_minutes_before
    },
    THIRTY_MINUTES_BEFORE{
        override fun getStringid(): Int =
            R.string.thirty_minutes_before
    },
    ONE_DAY_BEFORE{
        override fun getStringid(): Int =
            R.string.one_day_before
    },
    TWO_DAYS_BEFORE{
        override fun getStringid(): Int =
            R.string.two_days_before
    },
    CUSTOM_DAY_BEFORE{
        override fun getStringid(): Int =
            R.string.set_reminder_time
    };

    abstract fun getStringid(): Int
}

enum class ReminderScreenLockEnum{
    OFF,
    ON
}

@HiltViewModel
class NewTaskViewModel @Inject constructor(private val repository: TaskRepository) : ViewModel() {
    val subTasks: StateFlow<List<SubTaskEntity>> get() = _subTasks
    private val _subTasks = MutableStateFlow<MutableList<SubTaskEntity>>(mutableListOf())

    val canAddSubTask: StateFlow<Boolean> get() = _canAddSubTask
    private val _canAddSubTask = MutableStateFlow(false)

    val selectedDate = MutableStateFlow(Calendar.getInstance().time)
    val selectedHour = MutableStateFlow(-1)
    val selectedMinute = MutableStateFlow(-1)

    val reminderStatus = MutableStateFlow(ReminderStatusEnum.OFF)
    val reminderTimeMinuteBefore = MutableStateFlow(ReminderTimeEnum.NONE)
    val reminderType = MutableStateFlow(ReminderTypeEnum.NOTIFICATION)
    val reminderScreenLock = MutableStateFlow(ReminderScreenLockEnum.OFF)

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
}
