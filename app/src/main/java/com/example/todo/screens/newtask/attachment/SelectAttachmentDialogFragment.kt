package com.example.todo.screens.newtask.attachment

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.todo.R
import com.example.todo.base.BaseDialogFragment
import com.example.todo.data.models.entity.AttachmentType
import com.example.todo.databinding.LayoutSelectAttachmentBinding
import com.example.todo.screens.home.HomeActivity
import com.example.todo.screens.newtask.NewTaskActivity
import com.example.todo.screens.newtask.NewTaskViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectAttachmentDialogFragment : BaseDialogFragment<LayoutSelectAttachmentBinding>() {

    private val viewModel: NewTaskViewModel by activityViewModels()

    companion object {
        private const val READ_PHOTO_PERMISSION_CODE = 100
        private const val READ_VIDEO_PERMISSION_CODE = 101
        private const val READ_AUDIO_PERMISSION_CODE = 102
        private const val WRITE_PERMISSION_CODE = 103
    }

    var currentRequestCode = 0

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LayoutSelectAttachmentBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEvents()
    }

    private fun setEvents() = with(viewBinding) {
        lnSelectPhoto.setOnClickListener { selectPhotos() }
        lnSelectVideo.setOnClickListener { selectVideo() }
        lnSelectAudio.setOnClickListener { selectAudio() }
    }

    private fun selectPhotos() = with(viewBinding) {
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_PHOTO_PERMISSION_CODE, {
            checkWritePermission(READ_PHOTO_PERMISSION_CODE)
        })
    }

    private fun selectVideo() = with(viewBinding) {
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_VIDEO_PERMISSION_CODE, {
            checkWritePermission(READ_VIDEO_PERMISSION_CODE)
        })
    }

    private fun selectAudio() = with(viewBinding) {
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_AUDIO_PERMISSION_CODE, {
            checkWritePermission(READ_AUDIO_PERMISSION_CODE)
        })
    }

    private fun openBottomDialog(requestCode: Int) = with(viewBinding) {
        val bundle: Bundle = Bundle()
        val type = when (requestCode) {
            READ_PHOTO_PERMISSION_CODE -> AttachmentType.IMAGE.name
            READ_VIDEO_PERMISSION_CODE -> AttachmentType.VIDEO.name
            READ_AUDIO_PERMISSION_CODE -> AttachmentType.AUDIO.name
            else -> ""
        }
        if (type.isNotEmpty()){
            bundle.putString("type", type)
            findNavController().navigate(R.id.openAttachmentBottomDialog, bundle)
        }
    }

    // Function to check and request permission.
    private fun checkPermission(permission: String, requestCode: Int, callback: () -> Unit) =
        with(viewBinding) {
            if (context?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        permission
                    )
                } != PackageManager.PERMISSION_GRANTED) {
                // Requesting the permission
                requestPermissions(arrayOf(permission), requestCode)
            } else {
                callback()
            }
        }

    private fun checkWritePermission(requestCode: Int) =
        with(viewBinding) {
            currentRequestCode = requestCode
            if (context?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                } != PackageManager.PERMISSION_GRANTED) {
                // Requesting the permission
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_PERMISSION_CODE)
            } else {
               openBottomDialog(requestCode)
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        if (listOf<Int>(
                READ_PHOTO_PERMISSION_CODE,
                READ_AUDIO_PERMISSION_CODE,
                READ_VIDEO_PERMISSION_CODE
            ).contains(requestCode) && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            checkWritePermission(requestCode)
        }
        // If request is cancelled, the result arrays are empty.
        if (requestCode == WRITE_PERMISSION_CODE && (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
        ) {
            if (currentRequestCode != 0){
                openBottomDialog(currentRequestCode)
                currentRequestCode = 0
            }

        }
        return
    }
}
