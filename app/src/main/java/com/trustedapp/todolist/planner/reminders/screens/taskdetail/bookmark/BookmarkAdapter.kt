package com.trustedapp.todolist.planner.reminders.screens.taskdetail.bookmark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDiffCallBack
import com.trustedapp.todolist.planner.reminders.base.BaseViewHolder
import com.trustedapp.todolist.planner.reminders.data.models.entity.BookmarkEntity
import com.trustedapp.todolist.planner.reminders.databinding.ItemBookmarkBinding
import com.trustedapp.todolist.planner.reminders.utils.helper.getBookmarkIcon
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
            R.drawable.bg_ripple_white_stroke_grey_border_rounded_8
        } else {
            R.drawable.bg_white_rounded_8
        }
        imageBookmark.setBackgroundResource(bgId)
    }
}

class BookmarkDiffCallback : BaseDiffCallBack<BookmarkEntity>()
