package com.example.todo.screens.newtask.attachment

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.R
import com.example.todo.data.models.entity.AttachmentAlbumEntity
import com.example.todo.data.models.entity.AttachmentAlbumTypeEnum
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

    val imageAlbums: StateFlow<List<AttachmentAlbumEntity>> get() = _imageAlbums
    private val _imageAlbums = MutableStateFlow(
        listOf<AttachmentAlbumEntity>(
            AttachmentAlbumEntity(
                0, context.getString(
                    R.string.camera
                ), emptyList(), AttachmentAlbumTypeEnum.CAMERA
            )
        )
    )

    val isShowImageList: StateFlow<Boolean> get() = _isShowImageList
    private val _isShowImageList = MutableStateFlow<Boolean>(false)


    val list: StateFlow<List<AttachmentEntity>> get() = _list
    private val _list = MutableStateFlow(emptyList<AttachmentEntity>())

    val selectedList: StateFlow<List<AttachmentEntity>> get() = _selectedList
    private val _selectedList = MutableStateFlow(emptyList<AttachmentEntity>())

    init {
        setupData(type)
    }

    fun setupData(type: AttachmentType) {
        when (type) {
            AttachmentType.IMAGE -> getAllImageAlbum()
            AttachmentType.VIDEO -> getAllVideo()
            AttachmentType.AUDIO -> getAllAudio()
        }
    }

    /**
     * Query all album images
     */
    private fun loadAllImageAlbum(): MutableList<AttachmentAlbumEntity> {
        val images = loadImagesFromStorage()

        val albums = mutableListOf<AttachmentAlbumEntity>()

        val imageProjection = arrayOf(
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        val cursor = context.applicationContext.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            imageProjection,
            null,
            null,
            null
        )

        cursor.use {
            it?.let {
                val nameColumn =
                    it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                var bucketId = 1
                while (it.moveToNext()) {
                    val bucketName = it.getString(nameColumn)
                    if (albums.filter { it.name == bucketName }.isEmpty()) {
                        val data = images.filter { image -> image.bucketName == bucketName }
                        val album = AttachmentAlbumEntity(
                            bucketId,
                            bucketName,
                            data,
                            AttachmentAlbumTypeEnum.ALBUM
                        )
                        albums += album
                        bucketId++
                    }


                }
            }
        }
        cursor?.close()
        return albums
    }

    /**
     * Query images
     */
    private fun loadImagesFromStorage(): ArrayList<AttachmentEntity> {
        val imageProjection = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
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
                val absolutePathOfImageColumn =
                    it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                val bucketNameColumn =
                    it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                while (it.moveToNext()) {
                    val id = it.getInt(idColumn)
                    val name = it.getString(nameColumn)
                    val extension: String = name.substring(name.lastIndexOf("."))
                    val size = it.getString(sizeColumn)
                    val absolutePathOfImage = it.getString(absolutePathOfImageColumn)
                    val bucketName = it.getString(bucketNameColumn)
                    listImage.add(
                        AttachmentEntity(
                            id, name, extension,
                            absolutePathOfImage, 0, AttachmentType.IMAGE.name, size, 0, bucketName
                        )
                    )
                }
            }
        }
        cursor?.close()

        return listImage
    }

    /**
     * Query video
     */
    private fun loadVideoFromStorage(): MutableList<AttachmentEntity> {
        val videoList = mutableListOf<AttachmentEntity>()
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.MediaColumns.DATA,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME
        )

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
                val durationColumn =
                    it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val absolutePathOfVideoColumn =
                    it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                val bucketNameColumn =
                    it.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
                while (it.moveToNext()) {
                    // Get values of columns for a given video.
                    val id = it.getInt(idColumn)
                    val name = it.getString(nameColumn)
                    val extension: String = name.substring(name.lastIndexOf("."))
                    val size = it.getString(sizeColumn)
                    val duration = it.getInt(durationColumn)
                    val absolutePathOfVideo = it.getString(absolutePathOfVideoColumn)
                    val bucketName = it.getString(bucketNameColumn)
                    Log.e("queryVideoStorage- absolutePathOfVideo", absolutePathOfVideo)
                    videoList += AttachmentEntity(
                        id,
                        name,
                        extension,
                        absolutePathOfVideo,
                        0,
                        AttachmentType.VIDEO.name,
                        size,
                        duration, bucketName
                    )
                }

            }

        }
        query?.close()
        Log.e("queryVideoStorage", videoList.toString())
        return videoList
    }

    /**
     * query audio
     */
    private fun loadAudioFromStorage(): MutableList<AttachmentEntity> {
        val audioList = mutableListOf<AttachmentEntity>()
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.MediaColumns.DATA,
            MediaStore.Audio.Media.BUCKET_DISPLAY_NAME
        )

// Display videos in alphabetical order based on their display name.
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        val query = context.applicationContext.contentResolver.query(
            collection,
            projection,
            null,
            null,
            null
        )
        query?.use { cursor ->
            // Cache column indices.
            cursor?.let {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val nameColumn =
                    it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val durationColumn =
                    it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val absolutePathOfAudioColumn =
                    it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                val bucketNameColumn =
                    it.getColumnIndexOrThrow(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME)
                while (it.moveToNext()) {
                    // Get values of columns for a given video.
                    val id = it.getInt(idColumn)
                    val name = it.getString(nameColumn)
                    val extension: String = name.substring(name.lastIndexOf("."))
                    val size = it.getString(sizeColumn)
                    val duration = cursor.getInt(durationColumn)
                    val absolutePathOfAudio = it.getString(absolutePathOfAudioColumn)
                    val bucketName = it.getString(bucketNameColumn)
                    Log.e("queryVideoStorage- absolutePathOfAudio", absolutePathOfAudio)
                    audioList += AttachmentEntity(
                        id,
                        name,
                        extension,
                        absolutePathOfAudio,
                        0,
                        AttachmentType.AUDIO.name,
                        size,
                        duration,
                        bucketName
                    )
                }

            }
        }
        query?.close()
        Log.e("queryVideoStorage", audioList.toString())
        return audioList
    }

    /**
     * get all images
     */
    fun getAllImages() {
        viewModelScope.launch {
            _list.value += withContext(Dispatchers.IO) {
                loadImagesFromStorage()
            }
        }
    }

    /**
     * get all album image
     */
    fun getAllImageAlbum() {
        viewModelScope.launch {
            _imageAlbums.value += withContext(Dispatchers.IO) {
                loadAllImageAlbum()
            }
        }
    }

    /**
     * get all video
     */
    fun getAllVideo() {
        viewModelScope.launch {
            _list.value += withContext(Dispatchers.IO) {
                loadVideoFromStorage()
            }
        }
    }

    /**
     * get all audio
     */
    fun getAllAudio() {
        viewModelScope.launch {
            _list.value += withContext(Dispatchers.IO) {
                loadAudioFromStorage()
            }
        }
    }

    /**
     * select mutiple attachment
     */
    fun onSelect(entity: AttachmentEntity) {
        var selectListTmp = _selectedList.value.toMutableList()
        if (selectListTmp.contains(entity)) {
            selectListTmp.remove(entity)
        } else {
            selectListTmp.add(entity)
        }
        _selectedList.value = selectListTmp
    }

    /**
     * set default value for select list
     */
    fun setSelectedListDefault(attachments: List<AttachmentEntity> = emptyList()) {
        val defaultList = attachments.filter { it.type == type.name }
        _selectedList.value = defaultList
    }

    fun onShowOrHideImageList(isShowList: Boolean, entity: AttachmentAlbumEntity? = null) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isShowImageList.value = isShowList

                if (isShowList) {
                    if (entity != null && entity.data.isNotEmpty()) {
                        Log.e("onShowOrHideImageList", entity.toString())
                        _list.value = entity.data
                    }
                } else {
                    _list.value = emptyList()
                }
            }
        }
    }
}