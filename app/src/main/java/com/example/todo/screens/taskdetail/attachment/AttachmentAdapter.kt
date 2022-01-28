package com.example.todo.screens.taskdetail.attachment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.todo.base.BaseDiffCallBack
import com.example.todo.base.BaseViewHolder
import com.example.todo.data.models.entity.AttachmentEntity
import com.example.todo.databinding.ItemAttachmentBinding
import com.example.todo.utils.gone
import com.example.todo.utils.show
import javax.inject.Inject

class AttachmentAdapter @Inject constructor() :
    ListAdapter<AttachmentEntity, AttachmentViewHolder>(AttachmentDiffCallback()) {

    var isEditing = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentViewHolder {
        val itemViewBinding =
            ItemAttachmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttachmentViewHolder(itemViewBinding)
    }

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
        holder.displayData(getItem(position), isEditing)
    }
}

class AttachmentViewHolder(private val itemViewBinding: ItemAttachmentBinding) :
    BaseViewHolder<AttachmentEntity>(itemViewBinding) {

    override fun displayData(entity: AttachmentEntity) {

    }

    fun displayData(entity: AttachmentEntity, isEditing: Boolean) = with(itemViewBinding) {
        textAttachment.text = "${entity.name}.${entity.extension}"
        if (isEditing) imageRemove.show() else imageRemove.gone()
    }
}

class AttachmentDiffCallback() : BaseDiffCallBack<AttachmentEntity>()
