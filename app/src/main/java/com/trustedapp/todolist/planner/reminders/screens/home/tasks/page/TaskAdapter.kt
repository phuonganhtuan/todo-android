package com.trustedapp.todolist.planner.reminders.screens.home.tasks.page

import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDiffCallBack
import com.trustedapp.todolist.planner.reminders.base.BaseViewHolder
import com.trustedapp.todolist.planner.reminders.data.models.entity.BookmarkEntity
import com.trustedapp.todolist.planner.reminders.data.models.entity.BookmarkType
import com.trustedapp.todolist.planner.reminders.data.models.entity.TaskShort
import com.trustedapp.todolist.planner.reminders.databinding.ItemTaskBinding
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.helper.getBookmarkIcon
import com.trustedapp.todolist.planner.reminders.utils.helper.getCatName
import com.trustedapp.todolist.planner.reminders.utils.helper.getCategoryColor
import com.trustedapp.todolist.planner.reminders.utils.show
import javax.inject.Inject

class TaskAdapter @Inject constructor() :
    ListAdapter<TaskShort, TaskViewHolder>(TaskDiffCallback()) {

    private var onTaskInteractListener: OnTaskInteract? = null

    var isHideDay = false

    fun setOnTaskListener(onTaskInteract: OnTaskInteract) {
        onTaskInteractListener = onTaskInteract
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemViewBinding =
            ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(itemViewBinding, onTaskInteractListener)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.displayData(getItem(position), isHideDay)
    }
}

class TaskViewHolder(
    private val itemViewBinding: ItemTaskBinding,
    private val onTaskInteract: OnTaskInteract?
) :
    BaseViewHolder<TaskShort>(itemViewBinding) {

    private var taskId: Int = 0

    init {
        itemView.setOnClickListener {
            onTaskInteract?.onItemClick(taskId)
        }
        itemViewBinding.checkStatus.setOnClickListener {
            onTaskInteract?.onStatusChange(taskId)
        }
        itemViewBinding.buttonMark.setOnClickListener {
            onTaskInteract?.onMarkChange(taskId)
        }
    }

    override fun displayData(entity: TaskShort) {

    }

    fun displayData(entity: TaskShort, isHideDay: Boolean) = with(itemViewBinding) {
        textTaskTime.show()
        taskId = entity.task.id
        textTaskName.text = entity.task.title
        checkStatus.isChecked = entity.task.isDone
        textTaskTime.text = if (entity.task.calendar != null) {
            if (isHideDay) {
                if (entity.task.dueDate.isNullOrEmpty()) {
                    ""
                } else {
                    DateTimeUtils.getHourMinuteFromMillisecond(entity.task.calendar!!)
                }
            } else {
                if (entity.task.dueDate.isNullOrEmpty()) {
                    DateTimeUtils.getDayMonthFromMillisecond(entity.task.calendar!!)
                } else {
                    DateTimeUtils.getShortTimeFromMillisecond(entity.task.calendar!!)
                }
            }
        } else {
            ""
        }
        if (entity.category == null) {
            cardCat.gone()
        } else {
            cardCat.show()
            textCatName.text = getCatName(itemView.context, entity.category!!.name)
            val catColor = getCategoryColor(itemView.context, entity.category!!)
            textCatName.setTextColor(catColor)
            viewCatBg.setBackgroundColor(catColor)
        }
        if (entity.task.isDone) {
            cardCat.gone()
            textTaskTime.gone()
            val textFlag = textTaskName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            textTaskName.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.color_text_secondary
                )
            )
            textTaskName.paintFlags = textFlag
            textTaskTime.paintFlags = textFlag
        } else {
            val textFlag = textTaskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            textTaskName.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
            textTaskName.paintFlags = textFlag
            textTaskTime.paintFlags = textFlag
        }
        buttonMark.setImageDrawable(null)
        entity.bookmark?.let {
            val markIcon = if (entity.task.isMarked) {
                getBookmarkIcon(itemView.context, it)
            } else {
                getUnmarkIcon(it)
            }
            buttonMark.setImageDrawable(markIcon)
        }
    }

    private fun getUnmarkIcon(bookmark: BookmarkEntity): Drawable? {
        val iconId = when (bookmark.type) {
            BookmarkType.FLAG1.name -> R.drawable.ic_flag1_outline
            BookmarkType.FLAG2.name -> R.drawable.ic_flag2_outline
            BookmarkType.FLAG3.name -> R.drawable.ic_flag3_outline
            BookmarkType.NUMBER.name -> {
                when (bookmark.number) {
                    "1" -> R.drawable.ic_num1_outline
                    "2" -> R.drawable.ic_num2_outline
                    "3" -> R.drawable.ic_num3_outline
                    "4" -> R.drawable.ic_num4_outline
                    "5" -> R.drawable.ic_num5_outline
                    else -> return null
                }
            }
            else -> return null
        }
        return ContextCompat.getDrawable(itemView.context, iconId)
    }
}

class TaskDiffCallback : BaseDiffCallBack<TaskShort>() {

    override fun areContentsTheSame(oldItem: TaskShort, newItem: TaskShort): Boolean =
        oldItem.task.id == newItem.task.id
                && oldItem.task.isDone == newItem.task.isDone
                && oldItem.task.isMarked == newItem.task.isMarked
                && oldItem.task.title == newItem.task.title
                && oldItem.task.calendar == newItem.task.calendar
                && oldItem.task.categoryId == newItem.task.categoryId
                && oldItem.task.markId == newItem.task.markId

    override fun areItemsTheSame(oldItem: TaskShort, newItem: TaskShort): Boolean =
        oldItem.task.id == newItem.task.id
}

interface OnTaskInteract {
    fun onStatusChange(id: Int)
    fun onItemClick(id: Int)
    fun onMarkChange(id: Int)
}