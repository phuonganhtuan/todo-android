package com.example.todo.screens.newtask.attachment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.todo.R
import com.example.todo.base.BaseBottomSheetDialogFragment
import com.example.todo.data.models.entity.AttachmentType
import com.example.todo.databinding.LayoutSelectAttachmentListBinding
import com.example.todo.demo.attachments
import com.example.todo.screens.newtask.NewTaskViewModel
import com.example.todo.utils.gone
import com.example.todo.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SelectAttachmentBottomDialog :
    BaseBottomSheetDialogFragment<LayoutSelectAttachmentListBinding>() {

    private var type: AttachmentType? = null

    private val viewModel: NewTaskViewModel by activityViewModels()
    private var selectAttachmentListViewModel: SelectAttachmentListViewModel? = null

    var adapterPictureVideo: SelectAttachmentPictureVideoListAdapter? = null
    var adapterAudio: SelectAttachmentAudioListAdapter? = null

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LayoutSelectAttachmentListBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        setEvents()
        observeData()
    }

    private fun initView() = with(viewBinding) {
        when (arguments?.getString("type")) {
            AttachmentType.IMAGE.name -> {
                type = AttachmentType.IMAGE
                tvTitle.setText(getString(R.string.select_picture))
                adapterPictureVideo = SelectAttachmentPictureVideoListAdapter()
                recyclerSelectPictureVideo.show()
                recyclerSelectPictureVideo.adapter = adapterPictureVideo
            }
            AttachmentType.VIDEO.name -> {
                type = AttachmentType.VIDEO
                tvTitle.setText(getString(R.string.select_video))
                adapterPictureVideo = SelectAttachmentPictureVideoListAdapter()
                recyclerSelectPictureVideo.show()
                recyclerSelectPictureVideo.adapter = adapterPictureVideo
            }
            AttachmentType.AUDIO.name -> {
                type = AttachmentType.AUDIO
                tvTitle.setText(getString(R.string.select_audio))
                adapterAudio = SelectAttachmentAudioListAdapter()
                recyclerSelectAudio.show()
                recyclerSelectAudio.adapter = adapterAudio
            }
        }
    }

    private fun initData() = with(viewBinding) {
        selectAttachmentListViewModel =
            context?.let { type?.let { it1 -> SelectAttachmentListViewModel(it1, it) } }

        selectAttachmentListViewModel?.setSelectedListDefault(viewModel.attachments.value)
    }

    private fun setEvents() = with(viewBinding) {
        when (type) {
            AttachmentType.IMAGE, AttachmentType.VIDEO -> {
                adapterPictureVideo?.attachmentSelectListener = {
                    selectAttachmentListViewModel?.onSelect(it)
                }
            }
            AttachmentType.AUDIO -> {

            }
        }

        tvDone.setOnClickListener { onClickDone() }
        imgClose.setOnClickListener { onClickCancel() }
    }

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                selectAttachmentListViewModel?.list?.collect {
                    Log.e("observeData", it.toString())
                    if (it.isNotEmpty()) {
                        when (type) {
                            AttachmentType.IMAGE, AttachmentType.VIDEO -> adapterPictureVideo?.submitList(
                                it
                            )
                            AttachmentType.AUDIO -> adapterAudio?.submitList(it)
                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                selectAttachmentListViewModel?.selectedList?.collect {
                    Log.e("observeData - selectedList", it.toString())
                    if (it.isNotEmpty()){
                        viewBinding.tvDone.show()
                    }else{
                        viewBinding.tvDone.gone()
                    }
                    when (type) {
                        AttachmentType.IMAGE, AttachmentType.VIDEO -> {
                            adapterPictureVideo?.selectAttachments = it
                            adapterPictureVideo?.notifyDataSetChanged()
                        }
//                            AttachmentType.AUDIO -> {
//                                adapterAudio?.submitList(it)
//                                adapterAudio?.notifyDataSetChanged()
//                            }
                    }

                }
            }
        }
    }

    private fun onClickCancel() = with(viewBinding){
        dismiss()
    }

    private fun onClickDone() = with(viewModel){
        selectAttachmentListViewModel?.selectedList.let {
            if (it != null) {
                selectAttachments(it.value)
            }
        }
//        dismiss()
        findNavController().popBackStack()
        findNavController().popBackStack()
    }
}