package com.example.todo.screens.home.tasks.page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.todo.base.BaseDiffCallBack
import com.example.todo.base.BaseViewHolder
import com.example.todo.data.models.entity.TaskEntity
import com.example.todo.databinding.ItemTaskBinding
import javax.inject.Inject

class TaskAdapter @Inject constructor() :
    ListAdapter<TaskEntity, ActivityViewHolder>(ActivityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val itemViewBinding =
            ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActivityViewHolder(itemViewBinding)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.displayData(getItem(position))
    }
}

class ActivityViewHolder(private val itemViewBinding: ItemTaskBinding) :
    BaseViewHolder<TaskEntity>(itemViewBinding) {

    init {
        itemView.setOnClickListener {

        }
    }

    override fun displayData(entity: TaskEntity) = with(itemViewBinding) {

    }
}

class ActivityDiffCallback : BaseDiffCallBack<TaskEntity>()
