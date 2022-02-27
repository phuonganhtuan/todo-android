package com.trustedapp.todolist.planner.reminders.screens.language

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.trustedapp.todolist.planner.reminders.base.BaseViewHolder
import com.trustedapp.todolist.planner.reminders.databinding.ItemLanguageBinding
import javax.inject.Inject

class LanguageAdapter @Inject constructor() :
    ListAdapter<LanguageModel, LanguageVH>(LanguageDiffCallback()) {

    var onItemSelected: ((Int) -> Unit)? = null
    var selectedItem = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageVH {
        val itemViewBinding =
            ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguageVH(itemViewBinding, onItemSelected)
    }

    override fun onBindViewHolder(holder: LanguageVH, position: Int) {
        holder.displayData(getItem(position), selectedItem == position)
    }
}

class LanguageVH(
    private val itemViewBinding: ItemLanguageBinding,
    private val onItemSelected: ((Int) -> Unit)?
) : BaseViewHolder<LanguageModel>(itemViewBinding) {

    init {
        itemViewBinding.radioChoose.setOnClickListener {
            onItemSelected?.let { it(absoluteAdapterPosition) }
        }
    }

    override fun displayData(entity: LanguageModel) {
    }

    fun displayData(item: LanguageModel, isSelected: Boolean) = with(itemViewBinding) {
        Glide.with(itemView.context).load(item.flagId).into(imageFlag)
        textName.text = item.langName
        radioChoose.isChecked = isSelected
    }
}

class LanguageDiffCallback : DiffUtil.ItemCallback<LanguageModel>() {

    override fun areContentsTheSame(oldItem: LanguageModel, newItem: LanguageModel) =
        oldItem.id == newItem.id

    override fun areItemsTheSame(oldItem: LanguageModel, newItem: LanguageModel) =
        oldItem === newItem
}
