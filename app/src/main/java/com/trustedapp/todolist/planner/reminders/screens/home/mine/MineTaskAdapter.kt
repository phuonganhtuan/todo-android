package com.trustedapp.todolist.planner.reminders.screens.home.mine

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.trustedapp.todolist.planner.reminders.base.BaseViewHolder
import com.trustedapp.todolist.planner.reminders.data.models.entity.TaskShort
import com.trustedapp.todolist.planner.reminders.databinding.ItemTaskMineBinding
import com.trustedapp.todolist.planner.reminders.screens.home.tasks.page.TaskDiffCallback
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import javax.inject.Inject

class MineTaskAdapter @Inject constructor() :
    ListAdapter<TaskShort, MineTaskViewHolder>(TaskDiffCallback()) {

    var onTaskClickListener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MineTaskViewHolder {
        val itemViewBinding =
            ItemTaskMineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MineTaskViewHolder(itemViewBinding, onTaskClickListener)
    }

    override fun onBindViewHolder(holder: MineTaskViewHolder, position: Int) {
        holder.displayData(getItem(position))
    }
}

class MineTaskViewHolder(
    private val itemViewBinding: ItemTaskMineBinding,
    private val onTaskClickListener: ((Int) -> Unit)?
) :
    BaseViewHolder<TaskShort>(itemViewBinding) {

    private var taskId = -1

    init {
        itemView.setOnClickListener {
            onTaskClickListener?.let { it(taskId) }
        }
    }

    override fun displayData(entity: TaskShort) = with(itemViewBinding) {
        taskId = entity.task.id
        textTaskName.text = entity.task.title
        textTaskTime.text = if (entity.task.calendar != null) {
            DateTimeUtils.getDayMonthFromMillisecond(entity.task.calendar!!)
        } else {
            ""
        }
    }
}
