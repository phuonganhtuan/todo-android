package com.example.todo.screens.newtask.attachment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.todo.base.BaseBottomSheetDialogFragment
import com.example.todo.databinding.LayoutSelectAttachmentListBinding
import com.example.todo.utils.DateTimeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class SelectAttachmentBottomDialog :
    BaseBottomSheetDialogFragment<LayoutSelectAttachmentListBinding>() {

    private val viewModel: SelectAttachmentListViewModel by viewModels()

    lateinit var adapter: SelectAttachmentListAdapter
    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LayoutSelectAttachmentListBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeData()
    }

    private fun initView() = with(viewBinding) {
        recyclerSelectAttachment.adapter = adapter
    }

    private fun initData() = with(viewBinding) {

    }

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                list.collect {
                    Log.e("observeData", it.toString())
//                    if (it.isNotEmpty()) adapter.submitList(it)
                }
            }
        }
    }
}