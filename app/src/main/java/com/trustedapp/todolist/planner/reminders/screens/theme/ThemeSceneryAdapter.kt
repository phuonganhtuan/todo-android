package com.trustedapp.todolist.planner.reminders.screens.theme

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.trustedapp.todolist.planner.reminders.base.BaseViewHolder
import com.trustedapp.todolist.planner.reminders.databinding.ItemThemeSceneryBinding
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.show
import javax.inject.Inject

class ThemeSceneryAdapter @Inject constructor() :
    ListAdapter<Drawable, ThemeSceneryVH>(ThemeItemDiffCallback()) {

    var selectedIndex = -1

    var onItemSelected: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeSceneryVH {
        val itemViewBinding =
            ItemThemeSceneryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ThemeSceneryVH(itemViewBinding, onItemSelected)
    }

    override fun onBindViewHolder(holder: ThemeSceneryVH, position: Int) {
        holder.displayData(getItem(position), selectedIndex == position)
    }
}

class ThemeSceneryVH(
    private val itemViewBinding: ItemThemeSceneryBinding,
    private val onItemSelected: ((Int) -> Unit)?
) : BaseViewHolder<Drawable>(itemViewBinding) {

    init {
        itemView.setOnClickListener {
            onItemSelected?.let { it(absoluteAdapterPosition) }
        }
    }

    override fun displayData(entity: Drawable) {
    }

    fun displayData(entity: Drawable, isSelected: Boolean) {
        Glide.with(itemView).load(entity).into(itemViewBinding.imageItem)
        itemViewBinding.imageCheck.apply {
            if (isSelected) show() else gone()
        }
    }
}
