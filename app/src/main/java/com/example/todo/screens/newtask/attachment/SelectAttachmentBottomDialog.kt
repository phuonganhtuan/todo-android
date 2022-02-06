package com.example.todo.screens.newtask.attachment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.todo.base.BaseBottomSheetDialogFragment
import com.example.todo.data.models.entity.AttachmentType
import com.example.todo.databinding.LayoutSelectAttachmentListBinding
import com.example.todo.screens.newtask.NewTaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SelectAttachmentBottomDialog :
    BaseBottomSheetDialogFragment<LayoutSelectAttachmentListBinding>() {

    private val viewModel: NewTaskViewModel by activityViewModels()
    private var selectAttachmentListViewModel: SelectAttachmentListViewModel? = null

    var adapterPictureVideo: SelectAttachmentPictureVideoListAdapter? = null
    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LayoutSelectAttachmentListBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        observeData()
    }

    private fun initView() = with(viewBinding) {
        adapterPictureVideo = SelectAttachmentPictureVideoListAdapter()
        recyclerSelectPictureVideo.adapter = adapterPictureVideo
    }

    private fun initData() = with(viewBinding) {
        selectAttachmentListViewModel =
            context?.let { SelectAttachmentListViewModel(AttachmentType.VIDEO, it) }
    }

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                selectAttachmentListViewModel?.list?.collect {
                    Log.e("observeData", it.toString())
                    if (it.isNotEmpty()) adapterPictureVideo?.submitList(it)
                }
            }
        }
    }
}