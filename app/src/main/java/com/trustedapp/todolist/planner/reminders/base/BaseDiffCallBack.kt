package com.trustedapp.todolist.planner.reminders.base

import androidx.recyclerview.widget.DiffUtil
import com.trustedapp.todolist.planner.reminders.data.models.entity.BaseEntity

abstract class BaseDiffCallBack<T> : DiffUtil.ItemCallback<T>() {

    override fun areContentsTheSame(oldItem: T, newItem: T) =
        (oldItem as BaseEntity).id == (newItem as BaseEntity).id

    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem === newItem
}
