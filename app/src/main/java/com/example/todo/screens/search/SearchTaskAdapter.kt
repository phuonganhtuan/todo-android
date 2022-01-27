package com.example.todo.screens.search

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.todo.base.BaseDiffCallBack
import com.example.todo.base.BaseViewHolder
import com.example.todo.data.models.entity.TaskEntity
import com.example.todo.databinding.ItemTaskSearchBinding
import javax.inject.Inject

class SearchTaskAdapter @Inject constructor() :
    androidx.recyclerview.widget.ListAdapter<TaskEntity, SearchTaskViewHolder>(
        TaskEntityDiffCallback()
    ) {

    var taskSelectListener: ((Int, String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchTaskViewHolder {
        val itemViewBinding =
            ItemTaskSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchTaskViewHolder(itemViewBinding, taskSelectListener)
    }

    override fun onBindViewHolder(holder: SearchTaskViewHolder, position: Int) {
        holder.displayData(getItem(position))
    }
}

class SearchTaskViewHolder(
    private val itemViewBinding: ItemTaskSearchBinding,
    private val taskSelectListener: ((Int, String) -> Unit)?
) :
    BaseViewHolder<TaskEntity>(itemViewBinding) {

    private var taskId = 0
    private var taskName = ""

    init {
        itemViewBinding.textTaskTitle.setOnClickListener {
            taskSelectListener?.let { it(taskId, taskName) }
        }
    }

    override fun displayData(entity: TaskEntity) = with(itemViewBinding) {
        taskId = entity.id
        taskName = entity.title
        textTaskTitle.text = entity.title
    }
}

class TaskEntityDiffCallback : BaseDiffCallBack<TaskEntity>()
