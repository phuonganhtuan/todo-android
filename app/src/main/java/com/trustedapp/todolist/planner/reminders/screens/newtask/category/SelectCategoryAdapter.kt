package com.trustedapp.todolist.planner.reminders.screens.newtask.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDiffCallBack
import com.trustedapp.todolist.planner.reminders.base.BaseViewHolder
import com.trustedapp.todolist.planner.reminders.data.models.entity.CategoryEntity
import com.trustedapp.todolist.planner.reminders.databinding.ItemSelectCategoryBinding
import com.trustedapp.todolist.planner.reminders.utils.helper.getCatName
import com.trustedapp.todolist.planner.reminders.utils.helper.getCategoryColor
import javax.inject.Inject

class SelectCategoryAdapter @Inject constructor() :
    ListAdapter<CategoryEntity, SelectCategoryViewHolder>(CategoryDiffCallback()) {

    private var onCatInteractListener: OnCatInteractListener? = null

    var selectedIndex = -1

    fun setOnCatListener(onCatInteractListener: OnCatInteractListener) {
        this.onCatInteractListener = onCatInteractListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCategoryViewHolder {
        val itemViewBinding =
            ItemSelectCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectCategoryViewHolder(itemViewBinding, onCatInteractListener)
    }

    override fun onBindViewHolder(holder: SelectCategoryViewHolder, position: Int) {
        holder.displayData(getItem(position), selectedIndex == position)
    }
}

class SelectCategoryViewHolder(
    private val itemViewBinding: ItemSelectCategoryBinding,
    private val onCatInteractListener: OnCatInteractListener?
) :
    BaseViewHolder<CategoryEntity>(itemViewBinding) {

    init {
        itemView.setOnClickListener {
            onCatInteractListener?.onCatClick(absoluteAdapterPosition)
        }
    }

    override fun displayData(entity: CategoryEntity) {

    }

    fun displayData(entity: CategoryEntity, isSelected: Boolean) = with(itemViewBinding) {
        val catColor = getCategoryColor(itemView.context, entity)
        textCategory.text = getCatName(itemView.context, entity.name)
        textCategory.setTextColor(catColor)
        val bg = ContextCompat.getDrawable(
            itemView.context,
            if (isSelected) R.drawable.bg_greybg_rounded_8 else R.drawable.bg_white_rounded_8
        )
        textCategory.background = bg
    }
}

class CategoryDiffCallback : BaseDiffCallBack<CategoryEntity>()

interface OnCatInteractListener {
    fun onCatClick(index: Int)
}
