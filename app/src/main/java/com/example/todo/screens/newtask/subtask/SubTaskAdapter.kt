package com.example.todo.screens.newtask.subtask

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.todo.base.BaseDiffCallBack
import com.example.todo.base.BaseViewHolder
import com.example.todo.data.models.entity.SubTaskEntity
import com.example.todo.databinding.ItemEditSubTaskBinding
import javax.inject.Inject

class SubTaskAdapter @Inject constructor() :
    ListAdapter<SubTaskEntity, SubTaskViewHolder>(SubTaskDiffCallback()) {

    private var onTaskInteractListener: OnSubTaskInteract? = null

    fun setOnTaskListener(onTaskInteract: OnSubTaskInteract) {
        onTaskInteractListener = onTaskInteract
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTaskViewHolder {
        val itemViewBinding =
            ItemEditSubTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubTaskViewHolder(itemViewBinding, onTaskInteractListener)
    }

    override fun onBindViewHolder(holder: SubTaskViewHolder, position: Int) {
        holder.displayData(getItem(position))
    }
}

class SubTaskViewHolder(
    private val itemViewBinding: ItemEditSubTaskBinding,
    private val onTaskInteract: OnSubTaskInteract?
) :
    BaseViewHolder<SubTaskEntity>(itemViewBinding) {

    private var taskId: Int = 0

    init {

    }

    override fun displayData(entity: SubTaskEntity) = with(itemViewBinding) {

    }
}

class SubTaskDiffCallback : BaseDiffCallBack<SubTaskEntity>() {

    override fun areContentsTheSame(oldItem: SubTaskEntity, newItem: SubTaskEntity): Boolean =
        oldItem.id == newItem.id && oldItem.isDone == newItem.isDone && oldItem.name == newItem.name
}

interface OnSubTaskInteract {

}
