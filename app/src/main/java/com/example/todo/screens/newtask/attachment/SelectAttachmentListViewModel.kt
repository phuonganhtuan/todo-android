package com.example.todo.screens.newtask.attachment

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.models.entity.AttachmentEntity
import com.example.todo.data.models.entity.AttachmentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SelectAttachmentListViewModel @Inject constructor(
    val type: AttachmentType,
    val context: Context
) :
    ViewModel() {
    val list: StateFlow<List<AttachmentEntity>> get() = _list
    private val _list = MutableStateFlow(emptyList<AttachmentEntity>())

    init {
        getAttachmentFromStorage(type)
    }

    fun getAttachmentFromStorage(type: AttachmentType) {
        when (type) {
            AttachmentType.IMAGE -> getAllImages()
            AttachmentType.ALBUM -> getAllAlbum()
            AttachmentType.VIDEO -> getAllVideo()
        }
    }

    private fun queryImageStorage() {
        val imageProjection = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media._ID,
            MediaStore.MediaColumns.DATA
        )
        val imageSortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        val cursor = context.applicationContext.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            imageProjection,
            null,
            null,
            null
        )

        val listImage = arrayListOf<AttachmentEntity>()
        cursor.use {
            it?.let {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val dateColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                val absolutePathOfImageColumn =
                    it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                while (it.moveToNext()) {
                    val id = it.getInt(idColumn)
                    val name = it.getString(nameColumn)
                    val extension: String = name.substring(name.lastIndexOf("."))
                    val size = it.getString(sizeColumn)
                    val date = it.getString(dateColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id.toLong()
                    )
                    val absolutePathOfImage = it.getString(absolutePathOfImageColumn)
                    Log.e("queryImageStorage", name)

                    listImage.add(
                        AttachmentEntity(
                            id, name, extension,
                            absolutePathOfImage, 0, AttachmentType.IMAGE.name, size, date
                        )
                    )
                }
            }
        }
        Log.e("queryImageStorage", listImage.toString() )
        _list.value = listImage
    }

    fun getAllImages() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                queryImageStorage()
            }
        }
    }

    fun getAllAlbum(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

            }
        }
    }

    private fun queryVideoStorage(){
        val videoList = mutableListOf<AttachmentEntity>()
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_TAKEN,
            MediaStore.Video.Media.SIZE,
            MediaStore.MediaColumns.DATA
        )

// Display videos in alphabetical order based on their display name.
        val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

        val query = context.applicationContext.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )
        query?.use { cursor ->
            // Cache column indices.
            cursor?.let {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn =
                it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dateColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN)
            val absolutePathOfVideoColumn =
                it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            while (it.moveToNext()) {
                // Get values of columns for a given video.
                val id = it.getInt(idColumn)
                val name = it.getString(nameColumn)
                val extension: String = name.substring(name.lastIndexOf("."))
                val size = it.getString(sizeColumn)

                val date = it.getString(dateColumn)
                val absolutePathOfVideo = it.getString(absolutePathOfVideoColumn)
                videoList += AttachmentEntity(id, name, extension, absolutePathOfVideo, 0, AttachmentType.VIDEO.name, size, date)
            }}
        }
        Log.e("queryVideoStorage", videoList.toString())
        _list.value = videoList
    }

    fun getAllVideo(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                queryVideoStorage()
            }
        }
    }
}