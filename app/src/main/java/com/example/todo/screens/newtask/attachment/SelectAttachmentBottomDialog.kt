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
import com.example.todo.data.models.entity.AttachmentAlbumEntity
import com.example.todo.data.models.entity.AttachmentAlbumTypeEnum
import com.example.todo.data.models.entity.AttachmentType
import com.example.todo.databinding.LayoutSelectAttachmentListBinding
import com.example.todo.demo.attachments
import com.example.todo.screens.newtask.NewTaskViewModel
import com.example.todo.utils.gone
import com.example.todo.utils.hide
import com.example.todo.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter

@AndroidEntryPoint
class SelectAttachmentBottomDialog :
    BaseBottomSheetDialogFragment<LayoutSelectAttachmentListBinding>() {

    private var type: AttachmentType? = null

    private val viewModel: NewTaskViewModel by activityViewModels()
    private var selectAttachmentListViewModel: SelectAttachmentListViewModel? = null

    var adapterImageAlbum: SelectAttachmentAlbumListAdapter? = null
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

                adapterImageAlbum = SelectAttachmentAlbumListAdapter()
                recyclerSelectAlbum.adapter = adapterImageAlbum

//                tvTitle.setText(getString(R.string.select_picture))
                adapterPictureVideo = SelectAttachmentPictureVideoListAdapter()
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
            AttachmentType.IMAGE -> {
                adapterImageAlbum?.attachmentAlbumSelectListener = {
                    onSelectAlbum(it)
                }
                adapterPictureVideo?.attachmentSelectListener = {
                    selectAttachmentListViewModel?.onSelect(it)
                }
            }
            AttachmentType.VIDEO -> {
                adapterPictureVideo?.attachmentSelectListener = {
                    selectAttachmentListViewModel?.onSelect(it)
                }
            }
            AttachmentType.AUDIO -> {
                adapterAudio?.attachmentSelectListener = {
                    selectAttachmentListViewModel?.onSelect(it)
                }
            }
        }

        tvDone.setOnClickListener { onClickDone() }
        imgClose.setOnClickListener { onClickCancel() }
    }

    private fun observeData() = with(viewModel) {
        /**
         * Change album to images view or revert
         */
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                selectAttachmentListViewModel?.isShowImageList?.filter { type == AttachmentType.IMAGE }
                    ?.collect {
                        viewBinding.recyclerSelectAlbum.visibility =
                            if (it) View.GONE else View.VISIBLE
                        viewBinding.recyclerSelectPictureVideo.visibility =
                            if (it) View.VISIBLE else View.GONE
                        viewBinding.tvTitle.setText(
                            if (it) getString(R.string.select_picture) else getString(
                                R.string.select_album
                            )
                        )
                        viewBinding.imgClose.setImageResource(if (it) R.drawable.ic_arrow_left else R.drawable.ic_close)
                    }
            }
        }

        /**
         * Show List Albums
         */
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                selectAttachmentListViewModel?.imageAlbums?.filter { type == AttachmentType.IMAGE && selectAttachmentListViewModel?.isShowImageList?.value == false }
                    ?.collect {
                        Log.e("observeData", it.toString())
                        if (it.isNotEmpty()) {
                            adapterImageAlbum?.submitList(it)
                        }
                    }
            }
        }

        /**
         * Show List attachment
         */
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                selectAttachmentListViewModel?.list?.collect {
                    Log.e("observeData - list", it.toString())
                    if (it.isNotEmpty()) {
                        when (type) {
                            AttachmentType.IMAGE, AttachmentType.VIDEO -> {
                                adapterPictureVideo?.submitList(
                                    it
                                )
                            }
                            AttachmentType.AUDIO -> adapterAudio?.submitList(it)
                        }
                    }
                }
            }
        }

        /**
         * Select attachment
         */
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                selectAttachmentListViewModel?.selectedList?.collect {
                    Log.e("observeData - selectedList", it.toString())
                    if (it.isNotEmpty()) {
                        viewBinding.tvDone.show()
                    } else {
                        viewBinding.tvDone.gone()
                    }
                    when (type) {
                        AttachmentType.IMAGE, AttachmentType.VIDEO -> {
                            adapterPictureVideo?.selectAttachments = it
                            adapterPictureVideo?.notifyDataSetChanged()
                        }
                        AttachmentType.AUDIO -> {
                            adapterAudio?.selectAttachments = it
                            adapterAudio?.notifyDataSetChanged()
                        }
                    }

                }
            }
        }
    }

    private fun onClickCancel() = with(viewBinding) {
        if (type == AttachmentType.IMAGE && selectAttachmentListViewModel?.isShowImageList?.value == true) {
            selectAttachmentListViewModel?.onShowOrHideImageList(false)
        } else {
            dismiss()
        }

    }

    private fun onClickDone() = with(viewModel) {
        selectAttachmentListViewModel?.selectedList.let {
            if (it != null) {
                selectAttachments(it.value)
            }
        }
        // Back to new Task
        findNavController().popBackStack()
        findNavController().popBackStack()
    }

    private fun onSelectAlbum(entity: AttachmentAlbumEntity) {
        if (entity.type == AttachmentAlbumTypeEnum.CAMERA) {

        } else {
            selectAttachmentListViewModel?.onShowOrHideImageList(true, entity)
        }
    }
}