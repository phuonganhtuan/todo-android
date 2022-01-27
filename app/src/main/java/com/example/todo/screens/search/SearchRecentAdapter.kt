package com.example.todo.screens.search

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.todo.base.BaseDiffCallBack
import com.example.todo.base.BaseViewHolder
import com.example.todo.databinding.ItemRecentSearchBinding
import javax.inject.Inject

class SearchRecentAdapter @Inject constructor() :
    androidx.recyclerview.widget.ListAdapter<String, SearchRecentViewHolder>(
        RecentDiffCallback()
    ) {

    var recentSelectListener: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRecentViewHolder {
        val itemViewBinding =
            ItemRecentSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchRecentViewHolder(itemViewBinding, recentSelectListener)
    }

    override fun onBindViewHolder(holder: SearchRecentViewHolder, position: Int) {
        holder.displayData(getItem(position))
    }
}

class SearchRecentViewHolder(
    private val itemViewBinding: ItemRecentSearchBinding,
    private val recentSelectListener: ((String) -> Unit)?
) :
    BaseViewHolder<String>(itemViewBinding) {

    private var recent = ""

    init {
        itemViewBinding.layoutRoot.setOnClickListener {
            recentSelectListener?.let { it(recent) }
        }
    }

    override fun displayData(entity: String) = with(itemViewBinding) {
        recent = entity
        textKeyword.text = entity
    }
}

class RecentDiffCallback : BaseDiffCallBack<String>() {

    override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
    override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
}
