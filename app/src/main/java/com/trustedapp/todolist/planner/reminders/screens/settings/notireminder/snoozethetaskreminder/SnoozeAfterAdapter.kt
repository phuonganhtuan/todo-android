package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.snoozethetaskreminder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDiffCallBack
import com.trustedapp.todolist.planner.reminders.base.BaseViewHolder
import com.trustedapp.todolist.planner.reminders.data.models.model.SnoozeAfterModel
import com.trustedapp.todolist.planner.reminders.databinding.ItemSelectRadioBinding

class ItemSnoozeAfterViewHolder(
    private val itemViewBinding: ItemSelectRadioBinding,
    private val itemSelectListener: ((SnoozeAfterModel) -> Unit)?
) : BaseViewHolder<ItemSelectRadioBinding>(itemViewBinding) {
    private var entity: SnoozeAfterModel? = null

    init {
        itemView.setOnClickListener {
            itemSelectListener.let {
                if (it != null) {
                    (entity ?: null)?.let { it1 -> it(it1) }
                }
            }
        }
    }

    fun displayData(entity: SnoozeAfterModel, selectEntity: SnoozeAfterModel?) =
        with(itemViewBinding) {
            this@ItemSnoozeAfterViewHolder.entity = entity
            tvName.text = itemView.context.getString(entity.nameResId)

            val isSelect = selectEntity?.id == entity.id
            val background = if (isSelect)
                R.drawable.ic_checked_radio
            else R.drawable.ic_uncheck_radio
            imgRadio.setImageResource(background)
        }

    override fun displayData(entity: ItemSelectRadioBinding) {

    }


}

class SnoozeAfterAdapter :
    androidx.recyclerview.widget.ListAdapter<SnoozeAfterModel, ItemSnoozeAfterViewHolder>(
        SnoozeAfterDiffCallback()
    ) {

    var itemSelectListener: ((SnoozeAfterModel) -> Unit)? = null

    var selectEntity: SnoozeAfterModel? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemSnoozeAfterViewHolder {
        val itemViewBinding =
            ItemSelectRadioBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ItemSnoozeAfterViewHolder(itemViewBinding, itemSelectListener)
    }

    override fun onBindViewHolder(holder: ItemSnoozeAfterViewHolder, position: Int) {
        holder.displayData(getItem(position), selectEntity)
    }

}

class SnoozeAfterDiffCallback : BaseDiffCallBack<SnoozeAfterModel>() {
    override fun areItemsTheSame(oldItem: SnoozeAfterModel, newItem: SnoozeAfterModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SnoozeAfterModel, newItem: SnoozeAfterModel): Boolean {
        return oldItem.id == newItem.id
    }
}