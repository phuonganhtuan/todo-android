package com.trustedapp.todolist.planner.reminders.screens.taskdetail.attachment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.trustedapp.todolist.planner.reminders.base.BaseDiffCallBack
import com.trustedapp.todolist.planner.reminders.base.BaseViewHolder
import com.trustedapp.todolist.planner.reminders.data.models.entity.AttachmentEntity
import com.trustedapp.todolist.planner.reminders.databinding.ItemAttachmentBinding
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.show
import javax.inject.Inject

class AttachmentAdapter @Inject constructor() :
    ListAdapter<AttachmentEntity, AttachmentViewHolder>(AttachmentDiffCallback()) {

    var onAttachmentRemoveListener: ((Int) -> Unit)? = null
    var onAttachmentClickListener: ((Int) -> Unit)? = null

    var isEditing = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentViewHolder {
        val itemViewBinding =
            ItemAttachmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttachmentViewHolder(
            itemViewBinding,
            onAttachmentRemoveListener,
            onAttachmentClickListener
        )
    }

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
        holder.displayData(getItem(position), isEditing)
    }
}

class AttachmentViewHolder(
    private val itemViewBinding: ItemAttachmentBinding,
    private val onAttachmentRemoveListener: ((Int) -> Unit)?,
    private val onAttachmentClickListener: ((Int) -> Unit)?,
) :
    BaseViewHolder<AttachmentEntity>(itemViewBinding) {

    init {
        itemViewBinding.imageRemove.setOnClickListener {
            onAttachmentRemoveListener?.let { it(absoluteAdapterPosition) }
        }
        itemView.setOnClickListener {
            onAttachmentClickListener?.let { it(absoluteAdapterPosition) }
        }
    }

    override fun displayData(entity: AttachmentEntity) {

    }

    fun displayData(entity: AttachmentEntity, isEditing: Boolean) = with(itemViewBinding) {
        textAttachment.text = entity.name
        if (isEditing) imageRemove.show() else imageRemove.gone()
    }
}

class AttachmentDiffCallback : BaseDiffCallBack<AttachmentEntity>()
