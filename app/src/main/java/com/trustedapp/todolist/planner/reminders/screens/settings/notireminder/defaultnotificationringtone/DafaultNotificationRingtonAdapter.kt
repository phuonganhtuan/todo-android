package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.defaultnotificationringtone

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDiffCallBack
import com.trustedapp.todolist.planner.reminders.base.BaseViewHolder
import com.trustedapp.todolist.planner.reminders.data.models.entity.RingtoneEntity
import com.trustedapp.todolist.planner.reminders.databinding.ItemSelectRadioBinding

class ItemDafaultNotificationRingtonViewHolder(
    private val itemViewBinding: ItemSelectRadioBinding,
    private val itemSelectListener: ((RingtoneEntity) -> Unit)?
) : BaseViewHolder<ItemSelectRadioBinding>(itemViewBinding) {
    private var ringtoneEntity: RingtoneEntity? = null

    init {
        itemView.setOnClickListener {
            itemSelectListener.let {
                if (it != null) {
                    (ringtoneEntity ?: null)?.let { it1 -> it(it1) }
                }
            }
        }
    }

    fun displayData(entity: RingtoneEntity, selectEntity: RingtoneEntity?) =
        with(itemViewBinding) {
            ringtoneEntity = entity
            tvName.text = entity.name

            val isSelect = selectEntity?.id == entity.id
            val background = if (isSelect) ContextCompat.getDrawable(
                itemView.context,
                R.drawable.ic_checked_radio
            ) else ContextCompat.getDrawable(itemView.context, R.drawable.ic_uncheck_radio)
            imgRadio.background = background
        }

    override fun displayData(entity: ItemSelectRadioBinding) {

    }


}

class DafaultNotificationRingtonAdapter :
    androidx.recyclerview.widget.ListAdapter<RingtoneEntity, ItemDafaultNotificationRingtonViewHolder>(
        DafaultNotificationRingtonDiffCallback()
    ) {

    var itemSelectListener: ((RingtoneEntity) -> Unit)? = null

    var selectEntity: RingtoneEntity? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemDafaultNotificationRingtonViewHolder {
        val itemViewBinding =
            ItemSelectRadioBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ItemDafaultNotificationRingtonViewHolder(itemViewBinding, itemSelectListener)
    }

    override fun onBindViewHolder(holder: ItemDafaultNotificationRingtonViewHolder, position: Int) {
        holder.displayData(getItem(position), selectEntity)
    }

}

class DafaultNotificationRingtonDiffCallback : BaseDiffCallBack<RingtoneEntity>() {
    override fun areItemsTheSame(oldItem: RingtoneEntity, newItem: RingtoneEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RingtoneEntity, newItem: RingtoneEntity): Boolean {
        return oldItem.id == newItem.id
    }
}