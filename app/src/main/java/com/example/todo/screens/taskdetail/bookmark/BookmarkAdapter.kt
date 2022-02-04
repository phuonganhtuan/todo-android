package com.example.todo.screens.taskdetail.bookmark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.todo.R
import com.example.todo.base.BaseDiffCallBack
import com.example.todo.base.BaseViewHolder
import com.example.todo.data.models.entity.BookmarkEntity
import com.example.todo.databinding.ItemBookmarkBinding
import com.example.todo.utils.helper.getBookmarkIcon
import javax.inject.Inject

class BookmarkAdapter @Inject constructor() :
    ListAdapter<BookmarkEntity, BookmarkViewHolder>(BookmarkDiffCallback()) {

    var selectedIndex = -1

    var onBookmarkSelectListener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val itemViewBinding =
            ItemBookmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookmarkViewHolder(itemViewBinding, onBookmarkSelectListener)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        holder.displayData(getItem(position), selectedIndex == position)
    }
}

class BookmarkViewHolder(
    private val itemViewBinding: ItemBookmarkBinding,
    private val onBookmarkSelectListener: ((Int) -> Unit)?
) :
    BaseViewHolder<BookmarkEntity>(itemViewBinding) {

    init {
        itemViewBinding.imageBookmark.setOnClickListener {
            onBookmarkSelectListener?.let { it(absoluteAdapterPosition) }
        }
    }

    override fun displayData(entity: BookmarkEntity) {

    }

    fun displayData(entity: BookmarkEntity, isSelected: Boolean) = with(itemViewBinding) {
        val bookmarkIcon = getBookmarkIcon(itemView.context, entity)
        imageBookmark.setImageDrawable(bookmarkIcon)
        val bgId = if (isSelected) {
            R.drawable.bg_ripple_white_stroke_rounded_8
        } else {
            R.drawable.bg_white_rounded_8
        }
        imageBookmark.setBackgroundResource(bgId)
    }
}

class BookmarkDiffCallback : BaseDiffCallBack<BookmarkEntity>()
