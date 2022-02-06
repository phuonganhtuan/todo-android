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
    }

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
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_PHOTO_PERMISSION_CODE)
    }

    private fun selectVideo() = with(viewBinding) {
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_VIDEO_PERMISSION_CODE)
    }

    private fun selectAudio() = with(viewBinding) {
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_AUDIO_PERMISSION_CODE)
    }

    private fun openBottomDialog(requestCode: Int) = with(viewBinding) {
        findNavController().navigate(R.id.openAttachmentBottomDialog)
    }

    // Function to check and request permission.
    private fun checkPermission(permission: String, requestCode: Int) = with(viewBinding) {
       if (context?.let {
               ContextCompat.checkSelfPermission(
                   it,
                   permission
               )
           } != PackageManager.PERMISSION_GRANTED)
        {
            // Requesting the permission
            requestPermissions(arrayOf(permission), requestCode)
        } else {
            openBottomDialog(requestCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        // If request is cancelled, the result arrays are empty.
        if ((grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
        ) {
            openBottomDialog(requestCode)
        }
        return
    }
}
