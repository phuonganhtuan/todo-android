package com.example.todo.screens.newtask.attachment

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.todo.R
import com.example.todo.base.BaseDiffCallBack
import com.example.todo.base.BaseViewHolder
import com.example.todo.data.models.entity.AttachmentAlbumEntity
import com.example.todo.data.models.entity.AttachmentAlbumTypeEnum
import com.example.todo.data.models.entity.AttachmentEntity
import com.example.todo.data.models.entity.AttachmentType
import com.example.todo.databinding.ItemAttachmentAlbumViewBinding
import com.example.todo.databinding.ItemAttachmentAudioViewBinding
import com.example.todo.databinding.ItemAttachmentPictureViewBinding
import com.example.todo.utils.FileUtils
import com.example.todo.utils.gone
import com.example.todo.utils.show
import java.io.File
import java.math.BigDecimal

/**
 * Select Album List
 */
class ItemAttachmentAlbumViewHolder(
    private val itemViewBinding: ItemAttachmentAlbumViewBinding,
    private val attachmentAlbumSelectListener: ((AttachmentAlbumEntity) -> Unit)?
) :
    BaseViewHolder<ItemAttachmentAlbumViewBinding>(itemViewBinding) {

    private var attachmentAlbumEntity: AttachmentAlbumEntity? = null

    init {
        itemView.setOnClickListener {
            attachmentAlbumSelectListener.let {
                if (it != null) {
                    (attachmentAlbumEntity ?: null)?.let { it1 -> it(it1) }
                }
            }
        }
    }

    override fun displayData(entity: ItemAttachmentAlbumViewBinding) {
        TODO("Not yet implemented")
    }

    fun displayData(entity: AttachmentAlbumEntity) = with(itemViewBinding) {
        attachmentAlbumEntity = entity
        if (entity.type == AttachmentAlbumTypeEnum.CAMERA) {
            imgCamera.show()
        } else {
            imgCamera.gone()
            entity.data.let {
                if (it.isNotEmpty()) {
                    it.get(0).let {
                        Glide.with(itemView.context)
                            .load(it.path).skipMemoryCache(false).into(thumbAttachment)
                    }
                }
            }
        }
        tvAlbumTitle.text = (entity.name)
    }
}

class SelectAttachmentAlbumListAdapter :
    androidx.recyclerview.widget.ListAdapter<AttachmentAlbumEntity, ItemAttachmentAlbumViewHolder>(
        SelectAttachmentAlbumDiffCallback()
    ) {

    var attachmentAlbumSelectListener: ((AttachmentAlbumEntity) -> Unit)? = null

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
        return ItemAttachmentAlbumViewHolder(itemViewBinding, attachmentAlbumSelectListener)
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
        oldItem.id == newItem.id && oldItem.name == newItem.name
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
        itemViewBinding.imgPlayVideo.setOnClickListener {
            attachmentEntity.let {
                if (it != null) {
                    FileUtils.openAttachment(itemView.context, it)
                }
            }
        }
    }

    override fun displayData(entity: ItemAttachmentPictureViewBinding) {
        TODO("Not yet implemented")
    }

    fun displayData(entity: AttachmentEntity, selectAttachments: List<Int>) =
        with(itemViewBinding) {
            attachmentEntity = entity
            imgPlayVideo.visibility =
                if (entity.type == AttachmentType.IMAGE.name) View.GONE else View.VISIBLE
            // Show isSelected
            val isSelected = selectAttachments.contains(entity.id)
            val bgThumbAttachmentId =
                if (isSelected) R.drawable.bg_border_primary else R.drawable.bg_border_grey
            thumbAttachment.background =
                ContextCompat.getDrawable(itemView.context, bgThumbAttachmentId)

            val index =
                if (isSelected) (selectAttachments.indexOf(entity.id) + 1).toString() else ""
            val bgTvSelectedId =
                if (isSelected) R.drawable.bg_primary_rounded_20 else R.drawable.bg_rounded_border_white
            tvSelected.text = (index)
            tvSelected.background = ContextCompat.getDrawable(itemView.context, bgTvSelectedId)
            // Show Thumbnail

            if (entity.type == AttachmentType.IMAGE.name) Glide.with(itemView.context)
                .load(entity.path).skipMemoryCache(false).into(thumbAttachment) else Glide.with(
                itemView.context
            ).asBitmap().load(Uri.fromFile(File(entity.path))).into(thumbAttachment)
        }
}

class SelectAttachmentPictureVideoListAdapter :
    androidx.recyclerview.widget.ListAdapter<AttachmentEntity, ItemAttachmentPictureViewHolder>(
        SelectAttachmentDiffCallback()
    ) {
    var selectAttachments = emptyList<Int>()

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
        oldItem.id == newItem.id && oldItem.name == newItem.name
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

    fun displayData(entity: AttachmentEntity, selectAttachments: List<Int>) =
        with(itemViewBinding) {
            attachmentEntity = entity

            tvAudioName.text = (entity.name)

            val durationObj = entity.duration?.let { splitToComponentTimes(it.toBigDecimal()) }

            val duration =
                durationObj?.let {
                    if (it.get(0) == 0) String.format(
                        "%02d:%02d",
                        it.get(1),
                        it.get(2)
                    ) else String.format("%02d:%02d:%02d", it.get(0), it.get(1), it.get(2))
                };

            tvDescription.text = ("${entity.size}KB ${duration ?: ""}")

            // Show isSelected
            val isSelected = selectAttachments.contains(entity.id)
            val bgLnAudio =
                if (isSelected) R.drawable.bg_ripple_white_stroke_primary_border_rounded_8 else R.drawable.bg_ripple_white_stroke_grey_border_rounded_8
            lnSelectAudio.background = ContextCompat.getDrawable(itemView.context, bgLnAudio)
        }

    fun splitToComponentTimes(biggy: BigDecimal): IntArray? {
        val longVal: Long = biggy.toLong()
        val hours = longVal.toInt() / 3600
        var remainder = longVal.toInt() - hours * 3600
        val mins = remainder / 60
        remainder = remainder - mins * 60
        val secs = remainder
        return intArrayOf(hours, mins, secs)
    }
}

class SelectAttachmentAudioListAdapter :
    androidx.recyclerview.widget.ListAdapter<AttachmentEntity, ItemAttachmentAudioViewHolder>(
        SelectAttachmentAudioDiffCallback()
    ) {

    var selectAttachments = emptyList<Int>()

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
        oldItem.id == newItem.id && oldItem.name == newItem.name
}