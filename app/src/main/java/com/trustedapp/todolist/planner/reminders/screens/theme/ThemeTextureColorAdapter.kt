package com.trustedapp.todolist.planner.reminders.screens.theme

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.trustedapp.todolist.planner.reminders.base.BaseViewHolder
import com.trustedapp.todolist.planner.reminders.databinding.ItemThemeColorBinding
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.show
import javax.inject.Inject

class ThemeTextureColorAdapter @Inject constructor() :
    ListAdapter<Drawable, ThemeTextureColorVH>(ThemeItemDiffCallback()) {

    var selectedIndex = -1

    var onItemSelected: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeTextureColorVH {
        val itemViewBinding =
            ItemThemeColorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ThemeTextureColorVH(itemViewBinding, onItemSelected)
    }

    override fun onBindViewHolder(holder: ThemeTextureColorVH, position: Int) {
        holder.displayData(getItem(position), selectedIndex == position)
    }
}

class ThemeTextureColorVH(
    private val itemViewBinding: ItemThemeColorBinding,
    private val onItemSelected: ((Int) -> Unit)?
) : BaseViewHolder<Drawable>(itemViewBinding) {

    init {
        itemView.setOnClickListener {
            onItemSelected?.let { it(absoluteAdapterPosition) }
        }
    }

    override fun displayData(entity: Drawable) = with(itemViewBinding) {
    }

    fun displayData(entity: Drawable, isSelected: Boolean) {
        Glide.with(itemView).load(entity).into(itemViewBinding.imageItem)
        itemViewBinding.imageCheck.apply {
            if (isSelected) show() else gone()
        }
    }
}

class ThemeItemDiffCallback : DiffUtil.ItemCallback<Drawable>() {

    override fun areItemsTheSame(oldItem: Drawable, newItem: Drawable) = oldItem === newItem

    override fun areContentsTheSame(oldItem: Drawable, newItem: Drawable) = false
}
