package com.example.todo.screens.newtask.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.todo.base.BaseDiffCallBack
import com.example.todo.base.BaseViewHolder
import com.example.todo.data.models.entity.CategoryEntity
import com.example.todo.databinding.ItemSelectCategoryBinding
import com.example.todo.utils.helper.getCategoryColor
import javax.inject.Inject

class SelectCategoryAdapter @Inject constructor() :
    ListAdapter<CategoryEntity, SelectCategoryViewHolder>(CategoryDiffCallback()) {

    private var onCatInteractListener: OnCatInteractListener? = null

    fun setOnCatListener(onCatInteractListener: OnCatInteractListener) {
        this.onCatInteractListener = onCatInteractListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCategoryViewHolder {
        val itemViewBinding =
            ItemSelectCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectCategoryViewHolder(itemViewBinding, onCatInteractListener)
    }

    override fun onBindViewHolder(holder: SelectCategoryViewHolder, position: Int) {
        holder.displayData(getItem(position))
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

    override fun displayData(entity: CategoryEntity) = with(itemViewBinding) {
        val catColor = getCategoryColor(itemView.context, entity)
        textCategory.text = entity.name
        textCategory.setTextColor(catColor)
    }
}

class CategoryDiffCallback : BaseDiffCallBack<CategoryEntity>()

interface OnCatInteractListener {
    fun onCatClick(index: Int)
}
