package com.example.todo.screens.newtask.attachment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.todo.base.BaseDiffCallBack
import com.example.todo.base.BaseViewHolder
import com.example.todo.data.models.entity.AttachmentAlbumEntity
import com.example.todo.data.models.entity.AttachmentEntity
import com.example.todo.data.models.entity.AttachmentType
import com.example.todo.databinding.ItemAttachmentAlbumViewBinding
import com.example.todo.databinding.ItemAttachmentPictureViewBinding

import java.io.File

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
) : BaseViewHolder<ItemAttachmentPictureViewBinding>(itemViewBinding) {
    override fun displayData(entity: ItemAttachmentPictureViewBinding) {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun displayData(entity: AttachmentEntity) = with(itemViewBinding) {
        val imgFile = File(entity.path)
        if (imgFile.exists()) {
            if (entity.type == AttachmentType.IMAGE.name){
                val myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
                thumbAttachment.setImageBitmap(myBitmap)
            }else {
                val thumbnail: Bitmap =
                    root.context.applicationContext.contentResolver.loadThumbnail(
                        Uri.fromFile(imgFile), Size(itemView.measuredWidth, itemView.measuredWidth), null)
                thumbAttachment.setImageBitmap(thumbnail)
                imgPlayVideo.visibility = View.VISIBLE
            }
        }
    }
}

class SelectAttachmentPictureVideoListAdapter :
    androidx.recyclerview.widget.ListAdapter<AttachmentEntity, ItemAttachmentPictureViewHolder>(
        SelectAttachmentDiffCallback()
    ) {

    override fun onBindViewHolder(holder: ItemAttachmentPictureViewHolder, position: Int) {
//        holder.displayData(getItem(position))
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
        return ItemAttachmentPictureViewHolder(itemViewBinding)
    }
}

class SelectAttachmentDiffCallback : BaseDiffCallBack<AttachmentEntity>() {
    override fun areContentsTheSame(oldItem: AttachmentEntity, newItem: AttachmentEntity): Boolean =
        oldItem.id == newItem.id && oldItem.path == newItem.path
}