package com.example.todo.screens.newtask.attachment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.todo.R
import com.example.todo.base.BaseDiffCallBack
import com.example.todo.base.BaseViewHolder
import com.example.todo.data.models.entity.AttachmentEntity
import com.example.todo.databinding.ItemAttachmentViewBinding

class ItemAttachmentViewHolder(
    private val itemViewBinding: ItemAttachmentViewBinding,
) : BaseViewHolder<ItemAttachmentViewBinding>(itemViewBinding) {
    override fun displayData(entity: ItemAttachmentViewBinding) {
        TODO("Not yet implemented")
        entity.thumbAttachment.setImageResource(R.drawable.ic_balloon)
    }
}

class SelectAttachmentListAdapter :
    androidx.recyclerview.widget.ListAdapter<AttachmentEntity, ItemAttachmentViewHolder>(
        SelectAttachmentDiffCallback()
    ) {

    override fun onBindViewHolder(holder: ItemAttachmentViewHolder, position: Int) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAttachmentViewHolder {
        val itemViewBinding =
            ItemAttachmentViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemAttachmentViewHolder(itemViewBinding)
    }
}

class SelectAttachmentDiffCallback : BaseDiffCallBack<AttachmentEntity>() {
    override fun areContentsTheSame(oldItem: AttachmentEntity, newItem: AttachmentEntity): Boolean =
        oldItem.id == newItem.id && oldItem.path == newItem.path
}