package com.trustedapp.todolist.planner.reminders.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseViewHolder<T>(itemViewBinding: ViewBinding): RecyclerView.ViewHolder(itemViewBinding.root) {

    abstract fun displayData(entity: T)
}
