package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.defaultnotificationringtone

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDiffCallBack
import com.trustedapp.todolist.planner.reminders.base.BaseViewHolder
import com.trustedapp.todolist.planner.reminders.data.models.entity.RingtoneEntity
import com.trustedapp.todolist.planner.reminders.databinding.ItemSelectRadioBinding
import com.trustedapp.todolist.planner.reminders.databinding.ItemSelectRecordRingtoneBinding
import com.trustedapp.todolist.planner.reminders.utils.FileUtils
import javax.inject.Inject

class RecordRingtoneAdapter @Inject constructor() :
    androidx.recyclerview.widget.ListAdapter<RingtoneEntity, ItemRecordRingtoneViewHolder>(
        RecordRingtoneAdapterDiffCallback()
    ) {

    var itemSelectListener: ((RingtoneEntity) -> Unit)? = null
    var imgPlayListener: ((RingtoneEntity) -> Unit)? = null
    var imgDeleteListener: ((RingtoneEntity) -> Unit)? = null

    var selectEntity: RingtoneEntity? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemRecordRingtoneViewHolder {
        val itemViewBinding =
            ItemSelectRecordRingtoneBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ItemRecordRingtoneViewHolder(itemViewBinding, itemSelectListener, imgPlayListener, imgDeleteListener)
    }

    override fun onBindViewHolder(holder: ItemRecordRingtoneViewHolder, position: Int) {
        holder.displayData(getItem(position), selectEntity)
    }

}

class ItemRecordRingtoneViewHolder(private val itemViewBinding: ItemSelectRecordRingtoneBinding,
                                   private val itemSelectListener: ((RingtoneEntity) -> Unit)?,
                                   private val imgPlayListener: ((RingtoneEntity) -> Unit)?,
                                   private val imgDeleteListener: ((RingtoneEntity) -> Unit)?) :
    BaseViewHolder<ItemSelectRecordRingtoneBinding>(itemViewBinding) {
    private var ringtoneEntity: RingtoneEntity? = null

    init {
        itemView.setOnClickListener {
            itemSelectListener?.let {
                ringtoneEntity?.let { entity -> it(entity) }
            }
            imgPlayListener?.let {
                ringtoneEntity?.let { entity -> it(entity) }
            }
        }
        itemViewBinding.imageDelete.setOnClickListener {
            imgDeleteListener?.let {
                ringtoneEntity?.let { entity -> it(entity) }
            }
        }
    }

    override fun displayData(entity: ItemSelectRecordRingtoneBinding) {

    }

    fun displayData(entity: RingtoneEntity, selectEntity: RingtoneEntity?) =
        with(itemViewBinding) {
            ringtoneEntity = entity

            tvName.text = entity.name
//            tvDuration.text = FileUtils.getAudioFileLength(itemView.context, entity.ringtoneUri, true)

            val isSelect = selectEntity?.id == entity.id && entity.type == selectEntity.type
            val background = if (isSelect)
                R.drawable.ic_checked_radio
            else R.drawable.ic_uncheck_radio
            imgRadio.setImageResource(background)
        }
}

class RecordRingtoneAdapterDiffCallback : BaseDiffCallBack<RingtoneEntity>() {
    override fun areItemsTheSame(oldItem: RingtoneEntity, newItem: RingtoneEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RingtoneEntity, newItem: RingtoneEntity): Boolean {
        return oldItem.id == newItem.id
    }
}

