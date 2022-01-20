package com.example.todo.screens.home.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.todo.base.BaseDiffCallBack
import com.example.todo.base.BaseViewHolder
import com.example.todo.data.models.DemoEntity
import com.example.todo.databinding.ItemRandomActivityBinding
import javax.inject.Inject

class ActivityAdapter @Inject constructor() :
    ListAdapter<DemoEntity, ActivityViewHolder>(ActivityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val itemViewBinding =
            ItemRandomActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActivityViewHolder(itemViewBinding)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.displayData(getItem(position))
    }
}

class ActivityViewHolder(private val itemViewBinding: ItemRandomActivityBinding) :
    BaseViewHolder<DemoEntity>(itemViewBinding) {

    override fun displayData(entity: DemoEntity) = with(itemViewBinding) {
        textName.text = entity.activity ?: "No activity available now"
    }
}

class ActivityDiffCallback : BaseDiffCallBack<DemoEntity>()
