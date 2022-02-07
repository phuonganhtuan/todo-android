package com.example.todo.screens.newtask.attachment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.todo.R
import com.example.todo.base.BaseDiffCallBack
import com.example.todo.base.BaseViewHolder
import com.example.todo.data.models.entity.AttachmentAlbumEntity
import com.example.todo.data.models.entity.AttachmentEntity
import com.example.todo.data.models.entity.AttachmentType
import com.example.todo.databinding.ItemAttachmentAlbumViewBinding
import com.example.todo.databinding.ItemAttachmentAudioViewBinding
import com.example.todo.databinding.ItemAttachmentPictureViewBinding
import com.example.todo.utils.gone
import com.example.todo.utils.show

import java.io.File
import java.util.*

/**
 * Select Album List
 */
class ItemAttachmentAlbumViewHolder(private val itemViewBinding: ItemAttachmentAlbumViewBinding) :
    BaseViewHolder<ItemAttachmentAlbumViewBinding>(itemViewBinding) {
    override fun displayData(entity: ItemAttachmentAlbumViewBinding) {
        TODO("Not yet implemented")
    }

    fun displayData(entity: AttachmentAlbumEntity) = with(itemViewBinding) {

    }
}

class SelectAttachmentAlbumListAdapter :
    androidx.recyclerview.widget.ListAdapter<AttachmentAlbumEntity, ItemAttachmentAlbumViewHolder>(
        SelectAttachmentAlbumDiffCallback()
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemAttachmentAlbumViewHolder {
        val itemViewBinding =
            ItemAttachmentAlbumViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ItemAttachmentAlbumViewHolder(itemViewBinding)
    }

    override fun onBindViewHolder(holder: ItemAttachmentAlbumViewHolder, position: Int) {
        holder.displayData(getItem(position))
    }
}

class SelectAttachmentAlbumDiffCallback : BaseDiffCallBack<AttachmentAlbumEntity>() {
    override fun areContentsTheSame(
        oldItem: AttachmentAlbumEntity,
        newItem: AttachmentAlbumEntity
    ): Boolean =
        oldItem.id == newItem.id
}

/**
 * Select Picture/Video List
 */

class ItemAttachmentPictureViewHolder(
    private val itemViewBinding: ItemAttachmentPictureViewBinding,
    private val attachmentSelectListener: ((AttachmentEntity) -> Unit)?
) : BaseViewHolder<ItemAttachmentPictureViewBinding>(itemViewBinding) {

    private var attachmentEntity: AttachmentEntity? = null

    init {
        itemView.setOnClickListener {
            attachmentSelectListener.let {
                if (it != null) {
                    (attachmentEntity ?: null)?.let { it1 -> it(it1) }
                }
            }
        }
    }

    override fun displayData(entity: ItemAttachmentPictureViewBinding) {
        TODO("Not yet implemented")
    }

    fun displayData(entity: AttachmentEntity, selectAttachments: List<AttachmentEntity>) =
        with(itemViewBinding) {
            attachmentEntity = entity

            // Show Thumbnail
            imgPlayVideo.visibility =
                if (entity.type == AttachmentType.IMAGE.name) View.GONE else View.VISIBLE
            if (entity.type == AttachmentType.IMAGE.name) Glide.with(itemView.context)
                .load(entity.path).skipMemoryCache(false).into(thumbAttachment) else Glide.with(
                itemView.context
            ).asBitmap().load(Uri.fromFile(File(entity.path))).into(thumbAttachment)

            // Show isSelected
            val isSelected = selectAttachments.contains(entity)
            val index = if (isSelected) (selectAttachments.indexOf(entity) + 1).toString() else ""
            val bgTvSelectedId =
                if (isSelected) R.drawable.bg_primary_rounded_20 else R.drawable.bg_rounded_border_white
            val bgThumbAttachmentId =
                if (isSelected) R.drawable.bg_border_primary else R.drawable.bg_rounded_border_white

            tvSelected.setText(index)
            tvSelected.background = ContextCompat.getDrawable(itemView.context, bgTvSelectedId)
            thumbAttachment.background =
                ContextCompat.getDrawable(itemView.context, bgThumbAttachmentId)
        }
}

class SelectAttachmentPictureVideoListAdapter :
    androidx.recyclerview.widget.ListAdapter<AttachmentEntity, ItemAttachmentPictureViewHolder>(
        SelectAttachmentDiffCallback()
    ) {
    var selectAttachments = emptyList<AttachmentEntity>()

    var attachmentSelectListener: ((AttachmentEntity) -> Unit)? = null

    override fun onBindViewHolder(holder: ItemAttachmentPictureViewHolder, position: Int) {
        holder.displayData(getItem(position), selectAttachments)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemAttachmentPictureViewHolder {
        val itemViewBinding =
            ItemAttachmentPictureViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ItemAttachmentPictureViewHolder(itemViewBinding, attachmentSelectListener)
    }
}

class SelectAttachmentDiffCallback : BaseDiffCallBack<AttachmentEntity>() {
    override fun areContentsTheSame(oldItem: AttachmentEntity, newItem: AttachmentEntity): Boolean =
        oldItem.id == newItem.id && oldItem.path == newItem.path
}

/**
 * Select Audio List
 */

class ItemAttachmentAudioViewHolder(
    private val itemViewBinding: ItemAttachmentAudioViewBinding,
    private val attachmentSelectListener: ((AttachmentEntity) -> Unit)?
) : BaseViewHolder<ItemAttachmentAudioViewBinding>(itemViewBinding) {

    private var attachmentEntity: AttachmentEntity? = null

    init {
        itemView.setOnClickListener {
            attachmentSelectListener.let {
                if (it != null) {
                    (attachmentEntity ?: null)?.let { it1 -> it(it1) }
                }
            }
        }
    }

    override fun displayData(entity: ItemAttachmentAudioViewBinding) {
        TODO("Not yet implemented")
    }

    fun displayData(entity: AttachmentEntity, selectAttachments: List<AttachmentEntity>) = with(itemViewBinding) {
        tvAudioName.setText(entity.name)
        tvDescription.setText("${entity.name}KB ${entity.duration}")

        // Show isSelected
        val isSelected = selectAttachments.contains(entity)
        val bgLnAudio =
            if (isSelected) R.drawable.bg_primary_rounded_20 else R.drawable.bg_fade_grey
        lnSelectAudio.background = ContextCompat.getDrawable(itemView.context, bgLnAudio)
    }
}

class SelectAttachmentAudioListAdapter :
    androidx.recyclerview.widget.ListAdapter<AttachmentEntity, ItemAttachmentAudioViewHolder>(
        SelectAttachmentAudioDiffCallback()
    ) {

    var selectAttachments = emptyList<AttachmentEntity>()

    var attachmentSelectListener: ((AttachmentEntity) -> Unit)? = null

    override fun onBindViewHolder(holder: ItemAttachmentAudioViewHolder, position: Int) {
        holder.displayData(getItem(position), selectAttachments)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemAttachmentAudioViewHolder {
        val itemViewBinding =
            ItemAttachmentAudioViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ItemAttachmentAudioViewHolder(itemViewBinding, attachmentSelectListener)
    }
}

class SelectAttachmentAudioDiffCallback : BaseDiffCallBack<AttachmentEntity>() {
    override fun areContentsTheSame(oldItem: AttachmentEntity, newItem: AttachmentEntity): Boolean =
        oldItem.id == newItem.id && oldItem.path == newItem.path
}