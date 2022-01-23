package com.example.todo.screens.home.tasks.page

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import com.example.todo.R
import com.example.todo.base.BaseDiffCallBack
import com.example.todo.base.BaseViewHolder
import com.example.todo.data.models.entity.TaskShort
import com.example.todo.databinding.ItemTaskBinding
import com.example.todo.utils.DateTimeUtils
import javax.inject.Inject

class TaskAdapter @Inject constructor() :
    ListAdapter<TaskShort, ActivityViewHolder>(TaskDiffCallback()) {

    private var onTaskInteractListener: OnTaskInteract? = null

    fun setOnTaskListener(onTaskInteract: OnTaskInteract) {
        onTaskInteractListener = onTaskInteract
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val itemViewBinding =
            ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActivityViewHolder(itemViewBinding, onTaskInteractListener)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.displayData(getItem(position))
    }
}

class ActivityViewHolder(
    private val itemViewBinding: ItemTaskBinding,
    private val onTaskInteract: OnTaskInteract?
) :
    BaseViewHolder<TaskShort>(itemViewBinding) {

    private var taskId: Int = 0

    init {
        itemView.setOnClickListener {
            onTaskInteract?.onItemClick(taskId)
        }
        itemViewBinding.checkStatus.setOnCheckedChangeListener { _, _ ->
            onTaskInteract?.onStatusChange(taskId)
        }
        itemViewBinding.buttonMark.setOnClickListener {
            onTaskInteract?.onMarkChange(taskId)
        }
    }

    override fun displayData(entity: TaskShort) = with(itemViewBinding) {
        taskId = entity.task.id
        textTaskName.text = entity.task.title
        checkStatus.isChecked = entity.task.isDone
        textTaskTime.text = if (entity.task.calendar != null) {
            DateTimeUtils.getShortTimeFromMillisecond(entity.task.calendar!!)
        } else {
            ""
        }
        if (entity.task.isDone) {
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
}

interface OnTaskInteract {
    fun onStatusChange(id: Int)
    fun onItemClick(id: Int)
    fun onMarkChange(id: Int)
}