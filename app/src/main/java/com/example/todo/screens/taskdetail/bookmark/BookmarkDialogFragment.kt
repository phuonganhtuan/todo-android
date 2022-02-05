package com.example.todo.screens.taskdetail.bookmark

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.todo.R
import com.example.todo.base.BaseDialogFragment
import com.example.todo.data.models.entity.BookmarkType
import com.example.todo.databinding.LayoutBookmarkBinding
import com.example.todo.screens.newtask.NewTaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class BookmarkDialogFragment : BaseDialogFragment<LayoutBookmarkBinding>() {

    private val viewModel: NewTaskViewModel by activityViewModels()

    @Inject
    lateinit var flagsAdapter: BookmarkAdapter

    @Inject
    lateinit var numberAdapter: BookmarkAdapter

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LayoutBookmarkBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupEvents()
        observeData()
    }

    private fun initViews() = with(viewBinding) {
        recyclerFlags.apply {
            adapter = flagsAdapter
            itemAnimator = null
        }
        recyclerNumber.apply {
            adapter = numberAdapter
            itemAnimator = null
        }
    }

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            bookmarks.collect {
                val flags = it.filter { bm -> bm.type != BookmarkType.NUMBER.name }
                val numbers = it.filter { bm -> bm.type == BookmarkType.NUMBER.name }
                flagsAdapter.submitList(flags)
                numberAdapter.submitList(numbers)
                task.value.bookmark?.let { bm ->
                    val type = bm.type
                    if (type == BookmarkType.NUMBER.name) {
                        val index = numbers.indexOf(bm)
                        numberAdapter.selectedIndex = index
                        numberAdapter.notifyDataSetChanged()
                    } else {
                        val index = flags.indexOf(bm)
                        flagsAdapter.selectedIndex = index
                        flagsAdapter.notifyDataSetChanged()
                    }
                    selectedBookmark = bm
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupEvents() = with(viewBinding) {
        buttonCancel.setOnClickListener {
            dismiss()
        }
        buttonSave.setOnClickListener {
            viewModel.updateBookmark()
            showToastMessage(getString(R.string.marked))
            dismiss()
        }
        flagsAdapter.onBookmarkSelectListener = {
            viewModel.selectedBookmark =
                viewModel.bookmarks.value.filter { bm -> bm.type != BookmarkType.NUMBER.name }[it]
            numberAdapter.selectedIndex = -1
            numberAdapter.notifyDataSetChanged()
            flagsAdapter.notifyItemChanged(flagsAdapter.selectedIndex)
            flagsAdapter.selectedIndex = it
            flagsAdapter.notifyItemChanged(it)
        }
        numberAdapter.onBookmarkSelectListener = {
            viewModel.selectedBookmark =
                viewModel.bookmarks.value.filter { bm -> bm.type == BookmarkType.NUMBER.name }[it]
            flagsAdapter.selectedIndex = -1
            flagsAdapter.notifyDataSetChanged()
            numberAdapter.notifyItemChanged(numberAdapter.selectedIndex)
            numberAdapter.selectedIndex = it
            numberAdapter.notifyItemChanged(it)
        }
    }
}
