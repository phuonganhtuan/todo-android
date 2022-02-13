package com.trustedapp.todolist.planner.reminders.screens.newtask.attachment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseBottomSheetDialogFragment
import com.trustedapp.todolist.planner.reminders.base.LoadDataState
import com.trustedapp.todolist.planner.reminders.data.models.entity.AttachmentAlbumEntity
import com.trustedapp.todolist.planner.reminders.data.models.entity.AttachmentAlbumTypeEnum
import com.trustedapp.todolist.planner.reminders.data.models.entity.AttachmentType
import com.trustedapp.todolist.planner.reminders.databinding.LayoutSelectAttachmentListBinding
import com.trustedapp.todolist.planner.reminders.screens.newtask.NewTaskViewModel
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.hide
import com.trustedapp.todolist.planner.reminders.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class SelectAttachmentBottomDialog :
    BaseBottomSheetDialogFragment<LayoutSelectAttachmentListBinding>() {

    private var type: AttachmentType? = null

    private val viewModel: NewTaskViewModel by activityViewModels()
    private var selectAttachmentListViewModel: SelectAttachmentListViewModel? = null

    private var adapterImageAlbum: SelectAttachmentAlbumListAdapter? = null
    private var adapterPictureVideo: SelectAttachmentPictureVideoListAdapter? = null
    private var adapterAudio: SelectAttachmentAudioListAdapter? = null

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val REQUEST_IMAGE_CAPTURE = 1
    }

    lateinit private var currentPhotoPath: String

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
                recyclerSelectAlbum.layoutManager = GridLayoutManager(context, 3)

                adapterPictureVideo = SelectAttachmentPictureVideoListAdapter()
                recyclerSelectPictureVideo.adapter = adapterPictureVideo
                recyclerSelectPictureVideo.layoutManager = GridLayoutManager(context, 3)
            }
            AttachmentType.VIDEO.name -> {
                type = AttachmentType.VIDEO
                tvTitle.text = (getString(R.string.select_video))
                adapterPictureVideo = SelectAttachmentPictureVideoListAdapter()
                recyclerSelectPictureVideo.adapter = adapterPictureVideo
                recyclerSelectPictureVideo.layoutManager = GridLayoutManager(context, 3)
            }
            AttachmentType.AUDIO.name -> {
                type = AttachmentType.AUDIO
                tvTitle.text = (getString(R.string.select_audio))
                adapterAudio = SelectAttachmentAudioListAdapter()
                recyclerSelectAudio.adapter = adapterAudio
                recyclerSelectAudio.layoutManager = LinearLayoutManager(context)

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
        layoutRoot.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeData() = with(viewModel) {
        /**
         * Loading
         */
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                selectAttachmentListViewModel?.isLoading?.collect {
                    viewBinding.loadingBar.apply {
                        if (it == LoadDataState.LOADING) show() else gone()
                    }
                    when (type) {
                        AttachmentType.IMAGE -> {
                            viewBinding.recyclerSelectAlbum.apply {
                                if (it == LoadDataState.LOADING) gone() else show()
                            }
                        }
                        AttachmentType.VIDEO -> {
                            viewBinding.recyclerSelectPictureVideo.apply {
                                if (it == LoadDataState.LOADING || (it == LoadDataState.SUCCESS && selectAttachmentListViewModel?.list?.value?.isEmpty() == true)) gone() else show()
                            }
                            viewBinding.tvNodata.apply {
                                if (it == LoadDataState.SUCCESS && selectAttachmentListViewModel?.list?.value?.isEmpty() == true) show() else gone()
                            }
                        }
                        AttachmentType.AUDIO -> {
                            viewBinding.recyclerSelectAudio.apply {
                                if (it == LoadDataState.LOADING || (it == LoadDataState.SUCCESS && selectAttachmentListViewModel?.list?.value?.isEmpty() == true)) gone() else show()
                            }
                            viewBinding.tvNodata.apply {
                                if (it == LoadDataState.SUCCESS && selectAttachmentListViewModel?.list?.value?.isEmpty() == true) show() else gone()
                            }
                        }
                    }
                }
            }
        }

        /**
         * Change album to images view or revert
         */
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                selectAttachmentListViewModel?.isShowImageList?.filter { type == AttachmentType.IMAGE }
                    ?.collect {
                        viewBinding.recyclerSelectAlbum.apply {
                            if (it) gone() else show()
                        }
                        viewBinding.recyclerSelectPictureVideo.apply {
                            if (it) show() else gone()
                        }
                        viewBinding.tvTitle.text = (
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
                selectAttachmentListViewModel?.imageAlbums
                    ?.filter { type == AttachmentType.IMAGE && selectAttachmentListViewModel?.isShowImageList?.value == false }
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
                selectAttachmentListViewModel?.list
                    ?.collect {
                        Log.e("observeData - list", it.toString())
                        if (it.isNotEmpty()) {
                            when (type) {
                                AttachmentType.IMAGE, AttachmentType.VIDEO -> {
                                    adapterPictureVideo?.submitList(
                                        it
                                    )
                                }
                                AttachmentType.AUDIO -> {
                                    adapterAudio?.submitList(it)
                                }
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
                selectAttachmentListViewModel?.selectIds?.collect {
                    Log.e("observeData - selectedList", it.toString())
                    if (it.isNotEmpty()) {
                        viewBinding.tvDone.show()
                    } else {
                        viewBinding.tvDone.hide()
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
                selectAttachments(requireContext(), it.value)
            }
            Log.e("onClickDone - attachments", viewModel.attachments.toString())
        }
        // Back to new Task
        findNavController().popBackStack()
        findNavController().popBackStack()
    }

    private fun onSelectAlbum(entity: AttachmentAlbumEntity) {
        if (entity.type == AttachmentAlbumTypeEnum.CAMERA) {
            checkPermission()
        } else {
            selectAttachmentListViewModel?.onShowOrHideImageList(true, entity)
        }
    }

    private fun checkPermission() = with(viewBinding) {
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.CAMERA
                )
            } != PackageManager.PERMISSION_GRANTED) {
            // Requesting the permission
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            dispatchTakePictureIntent()
        }
    }

    /**
     * Create image temp file
     */
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    /**
     * Show camera to take picture
     */
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            context?.packageManager?.let {
                takePictureIntent.resolveActivity(it)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        Toast.makeText(context, "Not support Camera", Toast.LENGTH_SHORT).show()
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri? = context?.let { it1 ->
                            FileProvider.getUriForFile(
                                it1,
                                requireContext().packageName + ".provider",
                                it
                            )
                        }
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
            }
        }
    }

    /**
     * Callback Activity
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            galleryAddPic()
        }
        return
    }

    /**
     * Call RequestPermission
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_CODE && (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
        ) {
            dispatchTakePictureIntent()
        }
        return
    }

    private fun galleryAddPic() {
        if (currentPhotoPath.isNotEmpty()){
            selectAttachmentListViewModel?.getCameraPhotoToSelectList(currentPhotoPath, {
                // your codes here run on main Thread
                onClickDone()
            })
        }
    }

}


